package com.netas.sshbridge.controller;

import java.util.Properties;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.netas.sshbridge.model.dto.ResponseDTO;

@RestController
@RequestMapping("/api/1.0/sshbridge")
public class SshBridgeTestController {
	Channel channel;
	Session session;

	@PostMapping("/connect")
	public ResponseEntity<ResponseDTO> connect() {
		JSch jsch = new JSch();
		String user = "drip";
		String host = "10.254.182.20";

		try {
			/*
			 * HostKeyRepository hkr = jsch.getHostKeyRepository();
			 * 
			 * HostKey[] hks = hkr.getHostKey(); if (hks != null) {
			 * System.out.println("Host keys in " + hkr.getKnownHostsRepositoryID()); for
			 * (int i = 0; i < hks.length; i++) { HostKey hk = hks[i];
			 * System.out.println(hk.getHost() + " " + hk.getType() + " " +
			 * hk.getFingerPrint(jsch)); } System.out.println(""); }
			 */

			UserInfo ui = new UserInfo() {

				@Override
				public void showMessage(String message) {
					System.out.println("user info showMessage => " + message);
				}

				@Override
				public boolean promptYesNo(String message) {
					System.out.println("user info promptYesNo y/n => " + message);
					return true;
				}

				@Override
				public boolean promptPassword(String message) {
					System.out.println("user info promptPassword y/n => " + message);
					return true;
				}

				@Override
				public boolean promptPassphrase(String message) {
					System.out.println("user info promptPassphrase y/n => " + message);
					return true;
				}

				@Override
				public String getPassword() {
					System.out.println("user info getPassword");
					return "drip";
				}

				@Override
				public String getPassphrase() {
					System.out.println("user info getPassphrase");
					return "drip";
				}
			};

			Session session = jsch.getSession(user, host, 22);
			this.session = session;
			Properties config = new Properties();

	        // this setting will cause JSCH to automatically add all target servers' entry to the known_hosts file
	        config.put("StrictHostKeyChecking", "no");  
	        
	        session.setConfig(config);
			session.setPassword("drip");

			session.connect(30000);
			Channel channel = session.openChannel("shell");
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			this.channel = channel;
			channel.connect(3 * 1000);
			System.out.println("channel => " + channel.isConnected());

		} catch (JSchException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(new ResponseDTO(), HttpStatus.OK);
	}
	
	@PostMapping("/disconnect")
	public ResponseEntity<ResponseDTO> disconnect() throws Exception {
		channel.sendSignal("exit");
		channel.sendSignal("exit");
		channel.disconnect();
		this.session.disconnect();
		System.out.println("disconnected => " + session.isConnected());
		System.out.println("disconnected => " + channel.isConnected());
		return new ResponseEntity<ResponseDTO>(new ResponseDTO(), HttpStatus.OK);
	}
}
