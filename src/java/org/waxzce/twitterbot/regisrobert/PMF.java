/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.waxzce.twitterbot.regisrobert;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 *
 * @author waxzce
 */
public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}