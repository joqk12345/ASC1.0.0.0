package com.asc.app.util;

public class IpUtil {
	public static String intToIp(int ip) {
		int[] b = new int[4];
		b[0] = (ip >> 24) & 0xff;
		b[1] = (ip >> 16) & 0xff;
		b[2] = (ip >> 8) & 0xff;
		b[3] = ip & 0xff;
		return b[3] + "." + b[2] + "." + b[1] + "." + b[0];
	}
}
