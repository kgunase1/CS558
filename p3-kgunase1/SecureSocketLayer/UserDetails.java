package SecureSocketLayer;
import java.time.LocalDateTime;

public class UserDetails {
    public String userId;
    public String password;
    public LocalDateTime timeStamp;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userIdIn) {
		userId = userIdIn;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String passwordIn) {
		password = passwordIn;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStampIn) {
		timeStamp = timeStampIn;
	}

}
