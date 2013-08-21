package com.raweng.built;

import java.util.HashMap;

/**
 * Helper class to set event uid and event properties required for analytics.
 * 
 * @author raw engineering, Inc
 *
 */
public class BuiltEvent {


	protected String eventUid;

	/**
	 * Previous event uid.
	 * 
	 * <p>
	 * <b> Note:- </b> Previous event id is useful for funneling. It helps create a funnel analysis by tracking the user flow through datapoints in an app.
	 */
	protected String previousEventUid;

	/**
	 *  {@linkplain HashMap} contains properties of this event, property name as key and its respective value as value. 
	 */
	protected HashMap<String, Object> properties;

	/**
	 * Creates new {@link BuiltEvent} instance with specified event uid.
	 *  
	 * @param eventUid
	 * 					event uid.
	 */
	public BuiltEvent(String eventUid) {
		this.eventUid = eventUid;
	}


	/**
	 * To set previous event uid.
	 * 
	 * @param previousEventUid
	 * 							previous event uid.
	 * 
	 * <p>
	 * <b> Note:- </b> Previous event id is useful for funneling. It helps create a funnel analysis by tracking the user flow through datapoints in an app.

	 */
	public void setPreviousEventUid(String previousEventUid) {
		this.previousEventUid = previousEventUid;

	}


	/**
	 * To get previous event uid.
	 * 
	 * @return 
	 * 			previous event uid.
	 * <p>
	 * <b> Note:- </b> Previous event id is useful for funneling. It helps create a funnel analysis by tracking the user flow through datapoints in an app.
	 */

	public String getPreviousEventUid() {

		return previousEventUid;

	}

	/**
	 * To set event properties belonging to this {@linkplain BuiltEvent}.
	 * 
	 * @param eventProperties
	 *          			{@linkplain HashMap} contains properties of this event, property name as key and its respective value as value. 
	 */
	public void setProperties(HashMap<String, Object> eventProperties) {
		properties = eventProperties;
	}

	/**
	 * To get event properties belonging to this {@linkplain BuiltEvent}.
	 * 
	 * @return
	 * 			{@linkplain HashMap} instance contains properties of this event, property name as key and its respective value as value. 
	 */

	public HashMap<String, Object> getProperties() {
		return properties;
	}


}
