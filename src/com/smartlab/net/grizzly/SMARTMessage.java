package com.smartlab.net.grizzly;

import java.util.Arrays;


public class SMARTMessage {

	private byte S;
    private byte M;
    private byte A;
    private byte R;
    private byte T;

    private byte major;
    private byte minor;

    private byte flags;
    private byte value;

    private int bodyLength;
    private Long taskId;
    
    private byte[] body;

    public SMARTMessage() {
    }

    public SMARTMessage(byte major, byte minor,
            byte flags, byte value, Long taskId,byte[] body) {
        S = 'S';
        M = 'M';
        A = 'A';
        R = 'R';
        T = 'T';

        this.major = major;
        this.minor = minor;
        this.flags = flags;
        this.value = value;
        this.taskId = taskId;
        if(body!=null){
        	bodyLength = body.length;
        }else{
        	bodyLength=0;
        }
        
        this.body = body;
    }

    public byte[] getSMARTHeader() {
        byte[] giopHeader = new byte[5];
        giopHeader[0] = S;
        giopHeader[1] = M;
        giopHeader[2] = A;
        giopHeader[3] = R;
        giopHeader[4] = T;

        return giopHeader;
    }

    public void setSMARTHeader(byte S, byte M, byte A, byte R,byte T) {
        this.S = S;
        this.M = M;
        this.A = A;
        this.R = R;
        this.T = T;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public byte getMajor() {
        return major;
    }

    public void setMajor(byte major) {
        this.major = major;
    }

    public byte getMinor() {
        return minor;
    }

    public void setMinor(byte minor) {
        this.minor = minor;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    
    public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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
        final SMARTMessage other = (SMARTMessage) obj;
        if (this.S != other.S) {
            return false;
        }
        if (this.M != other.M) {
            return false;
        }
        if (this.A != other.A) {
            return false;
        }
        if (this.R != other.R) {
            return false;
        }
        if (this.T != other.T) {
            return false;
        }
        if (this.major != other.major) {
            return false;
        }
        if (this.minor != other.minor) {
            return false;
        }
        if (this.flags != other.flags) {
            return false;
        }
        if (this.value != other.value) {
            return false;
        }
        if (this.bodyLength != other.bodyLength) {
            return false;
        }
        if (this.taskId != other.taskId) {
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
        hash = 97 * hash + this.S;
        hash = 97 * hash + this.M;
        hash = 97 * hash + this.A;
        hash = 97 * hash + this.R;
        hash = 97 * hash + this.T;
        hash = 97 * hash + this.major;
        hash = 97 * hash + this.minor;
        hash = 97 * hash + this.flags;
        hash = 97 * hash + this.value;
        hash = 97 * hash + this.bodyLength;
        hash = 97 * hash + (this.body != null ? Arrays.hashCode(this.body) : 0);
        return hash;
    }
}
