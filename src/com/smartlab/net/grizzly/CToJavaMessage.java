package com.smartlab.net.grizzly;

import java.util.Arrays;

public class CToJavaMessage {
	private byte Z;
	private byte I;
	private byte G;

	private int bodyLength;
	private byte[] body;

	public CToJavaMessage(byte[] body) {
		this.Z = 'Z';
		this.I = 'I';
		this.G = 'G';
		if (body != null) {
			this.bodyLength = body.length;
		} else {
			this.bodyLength = 0;
		}

		this.body = body;
	}

	public CToJavaMessage() {

	}
	
	public byte[] getCToJavaMessageHeader(){
		byte[] giopHeader = new byte[3];
        giopHeader[0] = Z;
        giopHeader[1] = I;
        giopHeader[2] = G;
		return giopHeader;
	}
	
	public void setCToJavaMessageHeader(byte Z,byte I,byte G){
		this.Z=Z;
		this.I=I;
		this.G=G;
	}
	
	public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }
    
    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
        this.bodyLength=body.length;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CToJavaMessage other = (CToJavaMessage) obj;
        if (this.Z != other.Z) {
            return false;
        }
        if (this.I != other.I) {
            return false;
        }
        if (this.G != other.G) {
            return false;
        }
        if (this.bodyLength != other.bodyLength) {
            return false;
        }
        if (this.body != other.body && (this.body == null ||
                !Arrays.equals(this.body, other.body))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.Z;
        hash = 97 * hash + this.I;
        hash = 97 * hash + this.G;
        hash = 97 * hash + this.bodyLength;
        hash = 97 * hash + (this.body != null ? Arrays.hashCode(this.body) : 0);
        return hash;
    }

}
