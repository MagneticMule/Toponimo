package org.toponimo.client.models;

public abstract class Asset {
	
	private String name = null;
	private String localPath = null;
	private String remotePath = null;
	private String wordId = null;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocalPath() {
		return this.localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getRemotePath() {
		return this.remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getWordId() {
		return this.wordId;
	}
	public void setWordId(String wordId) {
		this.wordId = wordId;
	}


}
