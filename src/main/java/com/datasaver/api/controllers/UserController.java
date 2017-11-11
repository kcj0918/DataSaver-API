package com.datasaver.api.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.datasaver.api.controllers.forms.FindPasswordForm;
import com.datasaver.api.controllers.forms.SignInForm;
import com.datasaver.api.controllers.forms.SignOutForm;
import com.datasaver.api.controllers.forms.SignUpForm;
import com.datasaver.api.controllers.forms.UpdateFriendsForm;
import com.datasaver.api.controllers.responses.DefaultResponse;
import com.datasaver.api.controllers.responses.DefaultResponse.Status;
import com.datasaver.api.controllers.responses.data.GetFriendsResponseData;
import com.datasaver.api.controllers.responses.data.GetFriendsResponseData.Friend;
import com.datasaver.api.controllers.responses.data.GetFriendsResponseData.Friend.MostRecentlyUsedWiFi;
import com.datasaver.api.controllers.responses.data.GetFriendsResponseData.MyProfile;
import com.datasaver.api.controllers.responses.data.SignInResponseData;
import com.datasaver.api.domains.User;
import com.datasaver.api.domains.WiFi;
import com.datasaver.api.domains.views.FindFriendsView;
import com.datasaver.api.services.UserService;
import com.datasaver.api.services.WiFiService;
import com.datasaver.api.utils.auth.Auth;
import com.datasaver.api.utils.auth.JWT;
import com.datasaver.api.utils.log.ControllerLog;
import com.datasaver.api.utils.mail.Mail;
import com.datasaver.api.utils.password.Encryptor;
import com.datasaver.api.utils.password.PasswordGenerator;
import com.datasaver.api.utils.res.Strings;

import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("user")
@Api
public class UserController {
	@Autowired
	private UserService us;

	@Autowired
	private WiFiService ws;

	@Autowired
	private Mail m;

	@PostMapping("/sign/up")
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> signUp(@RequestBody SignUpForm suf) {
		if (us.findByEmail(suf.getEmail()) != null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.ALREADY_EXIST_EMAIL);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.SERVICE_UNAVAILABLE);
		}

		if (us.findByPhoneNumber(suf.getPhoneNumber()) != null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.ALREADY_EXIST_PHONE_NUMBER);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.SERVICE_UNAVAILABLE);
		}

		User u = new User();
		u.setName(suf.getName());
		u.setPhoneNumber(suf.getPhoneNumber());
		u.setEmail(suf.getEmail());
		u.setPassword(Encryptor.process(suf.getPassword()));
		us.save(u);

		DefaultResponse dr = new DefaultResponse();
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}

	@PostMapping("/sign/in")
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> signIn(@RequestBody SignInForm sif) {
		User u = us.findByEmailNPassword(sif.getEmail(), Encryptor.process(sif.getPassword()));

		if (u == null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.CAN_NOT_FOUND_USER);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.UNAUTHORIZED);
		}

		DefaultResponse dr = new DefaultResponse(new SignInResponseData(JWT.create(u.getIdx())));
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}

	@DeleteMapping("/sign/out")
	@Auth
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> signOut(@RequestHeader("Authorization") String token,
			@ApiIgnore User u, @RequestBody SignOutForm sof) {
		if (us.findByEmailNPassword(sof.getEmail(), Encryptor.process(sof.getPassword())) == null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.CAN_NOT_FOUND_USER);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.UNAUTHORIZED);
		}

		us.delete(u);

		DefaultResponse dr = new DefaultResponse();
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}

	@PutMapping("/find/password")
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> findPassword(@RequestBody FindPasswordForm fpf) {
		User u = us.findByNameNPhoneNumberNEmail(fpf.getName(), fpf.getPhoneNumber(), fpf.getEmail());

		if (u == null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.CAN_NOT_FOUND_USER);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.UNAUTHORIZED);
		}

		String newPassword = PasswordGenerator.create();

		if (!m.sendFromNoRely("[DataSaver] 비밀번호 찾기", "임시 비밀번호 : " + newPassword, u.getEmail())) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.FAIL_TO_SEND_EMAIL);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.SERVICE_UNAVAILABLE);
		}

		u.setPassword(Encryptor.process(newPassword));
		us.save(u);

		DefaultResponse dr = new DefaultResponse();
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}

	@GetMapping("/friend/list")
	@Auth
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> getFriendList(@RequestHeader("Authorization") String token,
			@ApiIgnore User u) {
		u = us.findByIdx(u.getIdx());
		MyProfile mp = new MyProfile(u.getIdx(), u.getName(), u.getProfileImg());

		Collection<FindFriendsView> ffvs = us.findFriendsByIdx(u.getIdx());
		int ffvsSize = ffvs.size();
		Friend[] fs = new Friend[ffvsSize];
		int i = 0;

		for (FindFriendsView ffv : ffvs) {
			WiFi w = ws.findMostRecentlyUsedByUidx(ffv.getIdx());

			if (w == null) {
				fs[i] = new Friend(ffv.getIdx(), ffv.getName(), ffv.getProfileImg(), null);
			}

			else {
				fs[i] = new Friend(ffv.getIdx(), ffv.getName(), ffv.getProfileImg(),
						new MostRecentlyUsedWiFi(w.getSsid(), w.getLatitude(), w.getLongitude()));
			}

			i++;
		}

		GetFriendsResponseData gfrd = new GetFriendsResponseData(mp, fs);

		DefaultResponse dr = new DefaultResponse(gfrd);
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}

	@PutMapping("/friend/list")
	@Auth
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> updateFriendList(@RequestHeader("Authorization") String token,
			@ApiIgnore User u, @RequestBody UpdateFriendsForm uff) {
		long[] fuidxs = uff.getFuidxs();
		long uidx = u.getIdx();

		for (long fuidx : fuidxs) {
			if (fuidx == uidx) {
				DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.CAN_NOT_MAKE_FRIEND_RELATION_YOURSELF);
				return new ResponseEntity<DefaultResponse>(dr, HttpStatus.SERVICE_UNAVAILABLE);
			}
		}

		Collection<User> friends = us.findByIdxs(uff.getFuidxs());

		if (friends.size() == 0) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.CAN_NOT_FOUND_USER);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.SERVICE_UNAVAILABLE);
		}

		u.setFriends(friends);
		us.save(u);

		DefaultResponse dr = new DefaultResponse();
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}

	@GetMapping("/info/{idx}")
	@Auth
	@ControllerLog
	public @ResponseBody ResponseEntity<DefaultResponse> getInfo(@RequestHeader("Authorization") String token,
			@ApiIgnore User u, @PathVariable("idx") long idx) {
		if (!us.isFriend(u.getIdx(), idx)) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.ONLY_FRIEND_RELATION_CAN_GET_INFO);
			return new ResponseEntity<DefaultResponse>(dr, HttpStatus.UNAUTHORIZED);
		}

		DefaultResponse dr = new DefaultResponse(us.findInfoByIdx(idx));
		return new ResponseEntity<DefaultResponse>(dr, HttpStatus.OK);
	}
}