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

package com.intel.stl.ui.common.view;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;

public class DialogFactory {
    private final static Logger log = LoggerFactory
            .getLogger(DialogFactory.class);

    private static ImageIcon INFO_ICON = UIImages.LOGO_64.getImageIcon();

    private static ImageIcon CONFIRM_ICON = UIImages.CONFIRM_DLG.getImageIcon();

    private static ImageIcon WARNING_ICON = UIImages.WARNING_DLG.getImageIcon();

    private static ImageIcon ERROR_ICON = UIImages.ERROR_DLG.getImageIcon();

    public static int OK_OPTION = 0;

    public static int CANCEL_OPTION = 1;

    private static Map<Component, DialogBuilder> errorDialogs =
            new HashMap<Component, DialogBuilder>();

    //
    // Confirmation dialog has two options: 'OK' and 'Cancel'
    // 'OK' will return '0'
    // 'Cancel' will return '1'
    //
    public static int showConfirmDialog(Component owner, String msg) {
        DialogBuilder infoDialog =
                new DialogBuilder(owner, STLConstants.K0671_CONFIRM.getValue(),
                        true, STLConstants.K0645_OK.getValue(),
                        STLConstants.K0621_CANCEL.getValue());

        infoDialog.appendText(msg);
        infoDialog.setImageIcon(DialogFactory.CONFIRM_ICON);
        infoDialog.getDialog().setVisible(true);
        return infoDialog.getButtonPressed();
    }

    //
    // Information dialog has just one option: 'OK'
    //
    public static void showInfoDialog(Component owner, String msg) {
        DialogBuilder infoDialog =
                new DialogBuilder(owner,
                        STLConstants.K0032_INFORMATIONAL.getValue(), true,
                        STLConstants.K0645_OK.getValue());

        infoDialog.appendText(msg);
        infoDialog.setImageIcon(DialogFactory.INFO_ICON);
        infoDialog.getDialog().setVisible(true);
    }

    // Same as Info dialog, except use warning icon.
    // Warning dialog is modal
    public static void showWarningDialog(Component owner, String msg) {
        DialogBuilder warningDialog =
                new DialogBuilder(owner, STLConstants.K0031_WARNING.getValue(),
                        true, STLConstants.K0645_OK.getValue());

        warningDialog.appendText(msg);
        warningDialog.setImageIcon(DialogFactory.WARNING_ICON);
        warningDialog.getDialog().setVisible(true);
    }

    //
    // The error dialog displays new errors currently in the buffer.
    // This error dialog is modeless by default.
    // If a modal error dialog is required, add another showErrorDialog()
    // method to construct one with boolean modalityType parameter.
    //
    public static void showErrorDialog(Component comp, String errorString) {
        if (errorString != null && !errorString.isEmpty()) {
            DialogBuilder dlg = getDialogBuilder(comp);
            dlg.appendText(errorString);
            dlg.show();
        }
    }

    public static void showErrorDialog(Component comp, Throwable e) {
        showErrorDialog(comp, Collections.singletonList(e));
    }

    public static void showErrorDialog(Component comp,
            Collection<? extends Throwable> errors) {
        DialogBuilder dlg = getDialogBuilder(comp);
        showErrors(dlg, errors);
    }

    /**
     * 
     * <i>Description:</i> show errors on a model dialog. This is used for
     * errors during App initialization where no Frame available. We need to
     * wait until user click OK and then go ahead shutdown the application
     * 
     * @param comp
     * @param errors
     */
    public static void showModalErrorDialog(Component comp,
            Collection<? extends Throwable> errors) {
        Component root = comp == null ? null : SwingUtilities.getRoot(comp);
        DialogBuilder dlg =
                new DialogBuilder(root, STLConstants.K0030_ERROR.getValue(),
                        true, STLConstants.K0645_OK.getValue(), null);
        dlg.setImageIcon(DialogFactory.ERROR_ICON);
        dlg.getDialog().setAlwaysOnTop(true);
        showErrors(dlg, errors);
    }

    private static void showErrors(DialogBuilder dlg,
            Collection<? extends Throwable> errors) {
        boolean toShow = false;
        for (Throwable e : errors) {
            if (e instanceof ExecutionException) {
                e = ((ExecutionException) e).getCause();
            }
            if (e instanceof InterruptedException) {
                // ignore InterruptedException
                continue;
            }
            if (e instanceof NullPointerException) {
                // hide NullPointerException, put it into log file
                log.error("Null Pointer", e);
                continue;
            }
            String errorString = StringUtils.getErrorMessage(e);
            dlg.appendText(errorString);
            if (!toShow) {
                toShow = true;
            }
        }

        if (toShow) {
            dlg.show();
        }
    }

    private static DialogBuilder getDialogBuilder(Component comp) {
        if (comp == null) {
            // if (null == errorDialog) {
            DialogBuilder errorDialog =
                    new DialogBuilder(STLConstants.K0645_OK.getValue());
            errorDialog.setImageIcon(DialogFactory.ERROR_ICON);
            errorDialog.setTitle(STLConstants.K0030_ERROR.getValue());
            // }
            return errorDialog;
        }

        Component root = SwingUtilities.getRoot(comp);
        DialogBuilder dlg = errorDialogs.get(root);
        if (dlg == null || !dlg.getDialog().isShowing()) {
            // Construct error dialog with modal=false by default
            dlg =
                    new DialogBuilder(root,
                            STLConstants.K0030_ERROR.getValue(), false,
                            STLConstants.K0645_OK.getValue(), null);
            dlg.setImageIcon(DialogFactory.ERROR_ICON);
            errorDialogs.put(root, dlg);
        }
        return dlg;
    }

    public static int showPasswordDialog(java.awt.Component owner,
            String title, java.awt.Component contentPanel) {
        DialogBuilder passwordDialog =
                new DialogBuilder(owner, title, true, contentPanel,
                        STLConstants.K0645_OK.getValue(),
                        STLConstants.K0621_CANCEL.getValue());
        passwordDialog.getDialog().setVisible(true);
        return passwordDialog.getButtonPressed();
    }

}
