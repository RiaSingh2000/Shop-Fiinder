package Models;

public class Messages {
    String msg,img,sender,receiver;

    public Messages(String msg, String img, String sender, String receiver) {
        this.msg = msg;
        this.img = img;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public String getImg() {
        return img;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
