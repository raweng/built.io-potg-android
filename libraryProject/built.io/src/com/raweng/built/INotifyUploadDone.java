package com.raweng.built;


/**
 * To notify {@link BuiltFile} class when media file upload completes.
 * 
 * @author raw engineering, Inc
 *
 */
public interface INotifyUploadDone {

	public void getResult(String key, FileObject value);
}
