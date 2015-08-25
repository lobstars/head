package com.lobstar.head.ssh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;



public class Utils {
	public static String mkdir(String host,String user,String password,String dirs) {
		SSHClient ssh = new SSHClient(host, user, password);
		List<String> cmdsToExecute = new ArrayList<String>();
		String cmd = "mkdir "+dirs;
        cmdsToExecute.add(cmd);
        String execute = ssh.execute(cmdsToExecute);
        String[] split = execute.split("\r");
        return split[split.length-2>0?split.length-2:0];
	}
	
	public static String shell(String host,String user,String password,String dir,String cmd) {
		String cd = "cd "+dir;
		SSHClient ssh = new SSHClient(host, user, password);
		List<String> cmdsToExecute = new ArrayList<String>();
        cmdsToExecute.add(cd);
        cmdsToExecute.add(cmd);
        String execute = ssh.execute(cmdsToExecute);
        String[] split = execute.split("\r");
        return split[split.length-2>0?split.length-2:0];
	}
	
	public static String scp(String host,String user,String password,String local,String remote) {
		
		Connection conn = new Connection(host);
		try {
			conn.connect();
			boolean auth = conn.authenticateWithPassword(user, password);
			System.out.println(auth);
			SCPClient client = conn.createSCPClient();
			File file = new File(local);
			putDir(conn,file.getAbsolutePath(), remote);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.close();
		return null;
	}
	
	public static String shell(String host,String user,String password,String shell) {
		String ret = null;
		Connection conn = new Connection(host);
		try {
			conn.connect();
			boolean auth = conn.authenticateWithPassword(user, password);
			if(auth) {
				Session session = conn.openSession();
				session.execCommand(shell);
				session.waitForCondition(ChannelCondition.EOF, 0);
				InputStream stderr = session.getStderr();
				int err = stderr.available();
				if(err>0) {
					byte[] bs = new byte[err];
					stderr.read(bs);
					ret = new String(bs);
				}else {
					InputStream stream = session.getStdout();
					int available = stream.available();
					byte[] bs = new byte[available];
					stream.read(bs);
					ret = new String(bs);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.close();
		
		return ret;
		
	}
	
	private static void putDir(Connection conn,String localDirectory,String remoteTargetDirectory) throws IOException {
		  File dir=new File(localDirectory);
		  final String[] fileList=dir.list();
		  for (  String file : fileList) {
		    final String fullFileName=localDirectory + "/" + file;
		    if (new File(fullFileName).isDirectory()) {
		      if (file.compareTo(".svn") != 0) {
		        final String subDir=remoteTargetDirectory + "/" + file;
		        Session sess=conn.openSession();
		        sess.execCommand("mkdir " + subDir);
		        sess.waitForCondition(ChannelCondition.EOF,0);
		        sess.close();
		        putDir(conn,fullFileName,subDir);
		      }
		    }
		 else {
		      SCPClient scpc=conn.createSCPClient();
		      scpc.put(fullFileName,remoteTargetDirectory);
		      System.out.println(fullFileName);
		    }
		  }
		}
	
	public static String ssh(String host,String user,String password,String cmd) throws Exception{
		Connection conn = new Connection(host);
		conn.connect();
		conn.authenticateWithPassword(user, password);
		Session session = conn.openSession();
		
		session.execCommand(cmd);
		session.waitForCondition(ChannelCondition.EOF,0);
		String ret = null;
		InputStream stderr = session.getStderr();
		int err = stderr.available();
		if(err>0) {
			byte[] bs = new byte[err];
			stderr.read(bs);
			ret = new String(bs);
		}else {
			InputStream stdout = session.getStdout();
			int out = stdout.available();
			byte[] bs = new byte[out];
			stdout.read(bs);
			ret = new String(bs);
		}
		return ret;
	}
	
	public static void main(String[] args) throws Exception{
		
	
	}
}
