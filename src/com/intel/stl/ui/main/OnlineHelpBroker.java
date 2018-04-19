/**
 * Copyright (c) 2015, Intel Corporation
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.stl.ui.main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.net.URL;
import java.util.Locale;

import javax.help.BadIDException;
import javax.help.DefaultHelpBroker;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.Map.ID;
import javax.help.UnsupportedOperationException;
import javax.help.WindowPresentation;

public class OnlineHelpBroker extends DefaultHelpBroker {

    private HelpMainWindow mw = null;

    public OnlineHelpBroker(HelpSet hs) {
        mw = (HelpMainWindow) HelpMainWindow.getPresentation(hs, null);
    }

    /**
     * Zero-argument constructor. It should be followed by a setHelpSet()
     * invocation.
     */

    public OnlineHelpBroker() {
        mw = (HelpMainWindow) HelpMainWindow.getPresentation(null, null);
    }

    /**
     * Get the WindowPresentation for this HelpBroker
     * 
     * @returns the window presentation for this object
     */
    @Override
    public WindowPresentation getWindowPresentation() {
        return mw;
    }

    /**
     * Returns the default HelpSet
     */
    @Override
    public HelpSet getHelpSet() {
        return mw.getHelpSet();
    }

    /**
     * Changes the HelpSet for this broker.
     * 
     * @param hs
     *            The HelpSet to set for this broker. A null hs is valid
     *            parameter.
     */
    @Override
    public void setHelpSet(HelpSet hs) {
        debug("setHelpSet");
        mw.setHelpSet(hs);
    }

    /**
     * Set the presentation attributes from a HelpSet.Presentation. The
     * HelpSet.Presentation must be in the current HelpSet.
     * 
     * @param hsPres
     *            The HelpSet.Presentation
     * @since 2.0
     */
    @Override
    public void setHelpSetPresentation(HelpSet.Presentation hsPres) {
        debug("setHelpSetPresentation");
        mw.setHelpSetPresentation(hsPres);
    }

    /**
     * Gets the locale of this component.
     * 
     * @return This component's locale. If this component does not have a
     *         locale, the defaultLocale is returned.
     * @see #setLocale
     */
    @Override
    public Locale getLocale() {
        return mw.getLocale();
    }

    /**
     * Sets the locale of this HelpBroker. The locale is propagated to the
     * presentation.
     * 
     * @param l
     *            The locale to become this component's locale. A null locale is
     *            the same as the defaultLocale.
     * @see #getLocale
     */
    @Override
    public void setLocale(Locale l) {
        mw.setLocale(l);
    }

    /**
     * Gets the font for this HelpBroker.
     */
    @Override
    public Font getFont() {
        return mw.getFont();
    }

    /**
     * Sets the font for this this HelpBroker.
     * 
     * @param f
     *            The font.
     */
    @Override
    public void setFont(Font f) {
        mw.setFont(f);
    }

    /**
     * Set the currentView to the navigator with the same name as the
     * <tt>name</tt> parameter.
     * 
     * @param name
     *            The name of the navigator to set as the current view. If nav
     *            is null or not a valid Navigator in this HelpBroker then an
     *            IllegalArgumentException is thrown.
     * @throws IllegalArgumentException
     *             if nav is null or not a valid Navigator.
     */
    @Override
    public void setCurrentView(String name) {
        mw.setCurrentView(name);
    }

    /**
     * Determines the current navigator.
     */
    @Override
    public String getCurrentView() {
        return mw.getCurrentView();
    }

    /**
     * Initializes the presentation. This method allows the presentation to be
     * initialized but not displayed. Typically this would be done in a separate
     * thread to reduce the intialization time.
     */
    @Override
    public void initPresentation() {
        mw.createHelpWindow();
    }

    /**
     * Displays the presentation to the user.
     */
    @Override
    public void setDisplayed(boolean b) {
        debug("setDisplayed");
        mw.setDisplayed(b);
    }

    /**
     * Determines if the presentation is displayed.
     */
    @Override
    public boolean isDisplayed() {
        return mw.isDisplayed();
    }

    /**
     * Requests the presentation be located at a given position.
     */
    @Override
    public void setLocation(Point p) {
        mw.setLocation(p);
    }

    /**
     * Requests the location of the presentation.
     * 
     * @returns Point the location of the presentation.
     */
    @Override
    public Point getLocation() {
        return mw.getLocation();
    }

    /**
     * Requests the presentation be set to a given size.
     */
    @Override
    public void setSize(Dimension d) {
        mw.setSize(d);
    }

    /**
     * Requests the size of the presentation.
     * 
     * @returns Point the location of the presentation.
     */
    @Override
    public Dimension getSize() throws UnsupportedOperationException {
        return mw.getSize();
    }

    /**
     * Requests the presentation be set to a given screen.
     */
    @Override
    public void setScreen(int screen) {
        mw.setScreen(screen);
    }

    /**
     * Requests the screen of the presentation.
     * 
     * @returns int the screen of the presentation.
     */
    @Override
    public int getScreen() throws UnsupportedOperationException {
        return mw.getScreen();
    }

    /**
     * Hides/Shows view.
     */
    @Override
    public void setViewDisplayed(boolean displayed) {
        mw.setViewDisplayed(displayed);
    }

    /**
     * Determines if the current view is visible.
     */
    @Override
    public boolean isViewDisplayed() {
        return mw.isViewDisplayed();
    }

    /**
     * Shows this ID as content relative to the (top) HelpSet for the HelpBroker
     * instance--HelpVisitListeners are notified.
     * 
     * @param id
     *            A string that identifies the topic to show for the loaded
     *            (top) HelpSet
     * @exception BadIDException
     *                The ID is not valid for the HelpSet
     */
    @Override
    public void setCurrentID(String id) throws BadIDException {
        debug("setCurrentID - string");
        mw.setCurrentID(id);
    }

    /**
     * Displays this ID--HelpVisitListeners are notified.
     * 
     * @param id
     *            a Map.ID indicating the URL to display
     * @exception InvalidHelpSetContextException
     *                if the current helpset does not contain id.helpset
     */
    @Override
    public void setCurrentID(ID id) throws InvalidHelpSetContextException {
        debug("setCurrentID - ID");
        mw.setCurrentID(id);
    }

    /**
     * Determines which ID is displayed (if any).
     */
    @Override
    public ID getCurrentID() {
        return mw.getCurrentID();
    }

    /**
     * Displays this URL. HelpVisitListeners are notified. The currentID changes
     * if there is a mathing ID for this URL
     * 
     * @param url
     *            The url to display. A null URL is a valid url.
     */
    @Override
    public void setCurrentURL(URL url) {
        debug("setCurrentURL");
        mw.setCurrentURL(url);
    }

    /**
     * Determines which URL is displayed.
     */
    @Override
    public URL getCurrentURL() {
        return mw.getCurrentURL();
    }

    @Override
    public void setActivationObject(Object comp) {
        mw.setActivationObject(comp);
    }

    /**
     * Set the activation window. If the window is an instance of a Dialog and
     * the is modal, modallyActivated help is set to true and ownerDialog is set
     * to the window. In all other instances modallyActivated is set to false
     * and ownerDialog is set to null.
     * 
     * @param window
     *            the activating window
     * @since 1.1
     */
    @Override
    public void setActivationWindow(Window window) {
        mw.setActivationWindow(window);
    }

    private static final boolean debug = false;

    private static void debug(Object msg) {
        if (debug) {
            System.err.println("OnlineHelpBroker: " + msg);
        }
    }

}
