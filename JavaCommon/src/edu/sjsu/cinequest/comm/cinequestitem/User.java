package edu.sjsu.cinequest.comm.cinequestitem;

import net.rim.device.api.util.Persistable;
import edu.sjsu.cinequest.comm.Action;
import edu.sjsu.cinequest.comm.Actions;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CallbackException;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.QueryManager;

public class User {
	private String email;
	private String password;
	private UserSchedule schedule;
	private boolean loggedIn;
	private boolean failedAuthorization; 
	
	// For Sync action
	public static final int SYNC_MERGE = 1;
	public static final int SYNC_SAVE = 2;
	public static final int SYNC_REVERT = 3;
	public static final int SYNC_CANCEL = 0;

	// key produced by: echo -n "edu.sjsu.cinequest.comm.cinequestitem.User" |
	// md5sum | cut -c1-16
	private static final long PERSISTENCE_KEY = 0x74dbb8dc65739d30L;

	public User() {
		schedule = new UserSchedule();
		UserData data = (UserData) Platform.getInstance().loadPersistentObject(
				PERSISTENCE_KEY);
		if (data == null) {
			email = "";
			password = "";
		} else {
			email = data.email;
			Object decryptedPassword = Platform.getInstance().crypt(
					data.encryptedPassword, /* decrypt = */true);
			if (decryptedPassword == null)
				password = "";
			else
				password = (String) decryptedPassword;
			for (int i = 0; i < data.scheduleItems.length; i++)
				schedule.add(data.scheduleItems[i], UserSchedule.CONFIRMED);
			schedule.setLastChanged(data.lastChanged);
			loggedIn = true;
		}
	}

	public UserSchedule getSchedule() {
		return schedule;
	}
	
	public String getEmail() {
		return email;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void logout() 
	{
		schedule = new UserSchedule(); 
		loggedIn = false;
		email = "";
		password = "";
	}
	
	/**
	 * Persists the schedule. Call this method when closing the app.
	 */
	public void persistSchedule() {
		if (!schedule.isSaved()) {
			UserData data = new UserData();
			data.email = email;
			data.encryptedPassword = Platform.getInstance().crypt(password,
			   /* decrypted = */ false);
			data.scheduleItems = schedule.getScheduleItems();
			data.lastChanged = schedule.getLastChanged();
			Platform.getInstance().storePersistentObject(PERSISTENCE_KEY, data);
			schedule.setSaved(true);
		}
	}

	/**
	 * Sync the user's schedule
	 * @param credentialsAction an action for getting user credentials
	 * with some dialog. Input and output are a User.Credentials object.
	 * @param syncAction an action for getting the user's sync preference
	 * (merge, discard phone, discard server)
	 * @param progressCallback the callback when a schedule query occurs
	 * @param resultCallback the callback for completion of the sync task
	 * @param queryManager
	 */
	// TODO: It would be better to have TWO callbacks: a 
	// progress monitor callback for running alongside the time-consuming
	// tasks and a callback for returning the actual result
	public void syncSchedule(final Action credentialsAction,
			final Action syncAction, 
	    final Callback progressCallback, 
	    final Callback resultCallback,
	    final QueryManager queryManager) {
		
		Action auth = loggedIn && !failedAuthorization 
			? Actions.nothing() 
			: Actions.andThen(credentialsAction, new Actions.Step() {
				public Object invoke(Object in) {
					Credentials creds = (Credentials) in;
					email = creds.email;
					password = creds.password;
					return in;
				}});
			
		Action load = Actions.withCallback(new Action() {
			public void start(Object in, Callback cb) {
				if (loggedIn)
					queryManager.saveSchedule(cb, email, password,
						schedule);
				else
					queryManager.getSchedule(cb, email, password);
			}}, progressCallback);
		
		// This variable is needed to pass the conflicting schedule
		// from checkConflict to resolveConflict
		// Note that the syncAction is in between the two
		final UserSchedule[] conflicting = new UserSchedule[1];
			
		final Action checkConflict = new Action() {
			public void start(Object in, Callback cb) {
				if (in == null) {
					failedAuthorization = true; 
						// Next save prompts for username/password
					cb.failure(new CallbackException("Login failed", CallbackException.ERROR));
					return;
				} else {
					failedAuthorization = false;
				}
				
				UserSchedule newSchedule = (UserSchedule) in;
				boolean conflict = false; 
				if (!loggedIn) {
					loggedIn = true; 
					
					if (schedule.isEmpty()) { // use schedule from server
						schedule = newSchedule;
						persistSchedule(); 
					} else if (newSchedule.isEmpty()) { // use schedule from device
						schedule.setSaved(false);
					} else { // non-empty schedule on server and device
						conflict = true;						
					}
				} else { 
					if (newSchedule.isUpdated()) {
						schedule = newSchedule; // Server accepted save
						persistSchedule();						
					} else {
						conflict = true;
					}
				}
				
				if (conflict) {
					conflicting[0] = newSchedule;
					cb.invoke(Boolean.TRUE); 											
				} else {
					cb.invoke(Boolean.FALSE); 
				}
			}};
			
		Action resolveConflict = new Action() {
			public void start(Object in, Callback cb) {				
				int response = ((Integer) in).intValue();
				if (response == SYNC_MERGE) { 
					schedule.mergeWith(conflicting[0]);
					schedule.setSaved(false);
				} else if (response == SYNC_SAVE) { 
					schedule.setLastChanged(conflicting[0].getLastChanged());
					schedule.setSaved(false);
				} else if (response == SYNC_REVERT) { 
					schedule = conflicting[0];
					persistSchedule();
				} 
				cb.invoke(null);				
			}
		};
		
		Action saveIfNeeded = Actions.withCallback(new Action() {
			public void start(Object in, Callback cb) {
				if (schedule.isSaved()) {
					cb.invoke(null);
				}
				else {
					persistSchedule();
					queryManager.saveSchedule(cb, email, password,
						schedule);
				}
			}
		}, progressCallback);
		
		Actions.andThen(auth, 
				Actions.andThen(load, 
						Actions.andThen(
								Actions.ifThen(checkConflict, Actions.andThen(syncAction, resolveConflict)), 
						saveIfNeeded
						)
				)
			).start(new Credentials(email, password), resultCallback);
	}

	public static class Credentials {
		public Credentials(String email, String password) {
			this.email = email;
			this.password = password;
		}
		public final String email;
		public final String password;
	}
	
	static class UserData implements Persistable {
		String email;
		Object encryptedPassword;
		Schedule[] scheduleItems;
		String lastChanged;
	}
}
