package com.datasaver.api.services.interfaces;

import com.datasaver.api.domains.DeviceBaseStationLog;

public interface DeviceBaseStationLogServiceInterface {
	public DeviceBaseStationLog findByIdx(long idx);

	public void save(DeviceBaseStationLog deviceBaseStationLog);

	public void delete(DeviceBaseStationLog deviceBaseStationLog);
}
