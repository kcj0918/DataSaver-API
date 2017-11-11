package com.datasaver.api.services;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datasaver.api.domains.User;
import com.datasaver.api.domains.WiFi;
import com.datasaver.api.repositories.WiFiRepository;
import com.datasaver.api.services.interfaces.WiFiServiceInterface;

@Service("WiFiService")
public class WiFiService implements WiFiServiceInterface {
	@Autowired
	private WiFiRepository wr;

	@Autowired
	private EntityManager em;

	@Override
	public WiFi findByIdx(long idx) {
		return wr.findOne(idx);
	}

	@Override
	public void save(WiFi wifi) {
		wr.save(wifi);
	}

	@Override
	public void delete(WiFi wifi) {
		wr.delete(wifi);
	}

	@Override
	public WiFi findByUser(User user) {
		return wr.findByUser(user);
	}

	@Override
	public WiFi findByMac(String mac) {
		return wr.findByMac(mac);
	}

	@Override
	public WiFi findMostRecentlyUsedByUidx(long uidx) {
		Query q = em.createNativeQuery(
				"SELECT * FROM WiFi AS w INNER JOIN WiFiConnectionLog AS wcl ON w.idx = wcl.widx WHERE w.uidx = ? AND wcl.type = 1 ORDER BY wcl.ts DESC LIMIT 1",
				WiFi.class);
		q.setParameter(1, uidx);

		try {
			return (WiFi) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}