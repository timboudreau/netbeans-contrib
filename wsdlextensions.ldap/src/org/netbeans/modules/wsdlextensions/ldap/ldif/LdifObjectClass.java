/*
 * LdifSchema.java
 * 
 * Created on Apr 30, 2007, 2:35:57 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.wsdlextensions.ldap.ldif;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.wsdlextensions.ldap.impl.SearchFilterAttribute;

/**
 *
 * @author Gary
 */
public class LdifObjectClass {

    private String mName;
    private String mDescription;
    private String mSuper;
    private String mLadpUrl;
    private List mMust = new ArrayList();
    private List mMay = new ArrayList();
    private List mSelected = new ArrayList();
    private List mResultSet = new ArrayList();

    public void clearSelect() {
        mSelected.clear();
        mResultSet.clear();
    }

    public boolean isSelected() {
        if (mSelected.size() < 1 & mResultSet.size() < 1) {
            return false;
        }
        return true;
    }

    public void addSelected(SearchFilterAttribute selectedAttr) {
        mSelected.add(selectedAttr);
    }

    public void removeSelected(SearchFilterAttribute selectedAttr) {
        mSelected.remove(selectedAttr);
    }

    public void removeSelected(String attrName) {
        for (int j = 0; j < mSelected.size(); j++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelected.get(j);
            if (sfa.getAttributeName().equals(attrName)) {
                mSelected.remove(j);
            }
        }
    }

    public void setAttPos(String attrName, int index) {
        for (int j = 0; j < mSelected.size(); j++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelected.get(j);
            if (sfa.getAttributeName().equals(attrName)) {
                sfa.setPositionIndex(index);
                break;
            }
        }
    }

    public void updateCompareOp(String attrName, String compareOp) {
        for (int j = 0; j < mSelected.size(); j++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelected.get(j);
            if (sfa.getAttributeName().equals(attrName)) {
                sfa.setCompareOp(compareOp);
                break;
            }
        }
    }

    public void increaseBracketDepth(String attrName) {
        for (int j = 0; j < mSelected.size(); j++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelected.get(j);
            if (sfa.getAttributeName().equals(attrName)) {
                sfa.setBracketDepth(sfa.getBracketDepth() + 1);
                break;
            }
        }
    }

    public void reduceBracketDepth(String attrName) {
        for (int j = 0; j < mSelected.size(); j++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelected.get(j);
            if (sfa.getAttributeName().equals(attrName)) {
                sfa.setBracketDepth(sfa.getBracketDepth() - 1);
                break;
            }
        }
    }

    public void addResultSet(Object attr) {
        mResultSet.add(attr);
    }

    public void removeResultSet(Object attr) {
        mResultSet.remove(attr);
    }

    public void removeResultSet(String attr) {
        mResultSet.remove(attr);
    }

    public LdifObjectClass() {
    }

    public void setName(String n) {
        mName = n;
    }

    public void setDescription(String d) {
        mDescription = d;
    }

    public void setSuper(String s) {
        mSuper = s;
    }

    public void setLdapUrl(String s) {
        mLadpUrl = s;
    }

    public void addMust(String m) {
        if (mMust == null) {
            mMust = new ArrayList();
        }

        mMust.add(m);
    }

    public void addMay(String m) {
        if (mMay == null) {
            mMay = new ArrayList();
        }

        mMay.add(m);
    }

    public void setSelected(List s) {
        mSelected = s;
    }

    public void setResultSet(List r) {
        mResultSet = r;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getSuper() {
        return mSuper;
    }

    public String getLdapUrl() {
        return mLadpUrl;
    }

    public List getMust() {
        return mMust;
    }

    public List getMay() {
        return mMay;
    }

    public List getSelected() {
        return mSelected;
    }

    public List getResultSet() {
        return mResultSet;
    }
}
