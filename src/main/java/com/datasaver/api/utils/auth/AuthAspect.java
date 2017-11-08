package com.datasaver.api.utils.auth;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthAspect {/*
	@Autowired
	private HttpServletRequest hsr;

	@Autowired
	private UserService us;

	@Around("@annotation(com.datasaver.api.utils.auth.Auth)")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		String jwt = hsr.getHeader("Authorization");

		if (jwt == null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.MUST_NEED_JWT);
			return new ResponseEntity<>(dr, HttpStatus.SERVICE_UNAVAILABLE);
		}

		Token t = JWT.decode(jwt);

		if (t == null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.FAIL_TO_DECODE_TOKEN);
			return new ResponseEntity<>(dr, HttpStatus.SERVICE_UNAVAILABLE);
		}

		User u = us.getUser(t.getUidx());

		if (u == null) {
			DefaultResponse dr = new DefaultResponse(Status.FAIL, Strings.CAN_NOT_FOUND_USER);
			return new ResponseEntity<>(dr, HttpStatus.UNAUTHORIZED);
		}

		Object[] params = pjp.getArgs();

		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof User) {
				params[i] = u;

				break;
			}
		}

		return pjp.proceed(params);
	}*/
}