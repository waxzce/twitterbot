/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waxzce.twitterbot.regisrobert;

import com.google.appengine.api.datastore.Key;
import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 *
 * @author waxzce
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SimpleData {

    @Persistent
    @PrimaryKey
    private String realkey;
    @Persistent
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRealkey() {
        return realkey;
    }

    public void setRealkey(String realkey) {
        this.realkey = realkey;
    }
}
