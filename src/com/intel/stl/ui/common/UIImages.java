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

package com.intel.stl.ui.common;

import java.awt.Image;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public enum UIImages {
    LOGO_128("FMGUI_128x128.png"),
    LOGO_64("FMGUI_64x64.png"),
    LOGO_32("FMGUI_32x32.png"),
    LOGO_24("FMGUI_24x24.png"),
    HELP_ICON("help_16x16.png"), // Images used in the Swing plugin
    HFI_ICON("18x18-host.png"),
    HFI_GROUP_ICON("systemImage_16x16.png"),
    SW_ICON("switch_16x16.png"),
    SW_GROUP_ICON("switch_16x16.png"),
    ROUTER_ICON("router_16x16.png"),
    PIN_ICON("pin_16x16.png"),
    DOWN_ICON("down_16x16.png"),
    UP_ICON("up_16x16.png"),
    PORT_ICON("port_16x16.png"),
    INACTIVE_PORT_ICON("port_inactive_9x9.png"),
    SUBNET_ICON("subnet_16x16.png"),
    DEVICE_GROUP_ICON("device_group_16x16.png"),
    DEVICE_TYPE_ICON("device_types_16x16.png"),
    VIRTUAL_FABRIC_ICON("vf_16x16.png"),
    CRITICAL_ICON("critical_16x16.png"),
    ERROR_ICON("error_16x16.png"),
    WARNING_ICON("warning_16x16.png"),
    WARNING2_ICON("warning2_16x16.png"),
    NORMAL_ICON("normal_16x16.png"),
    OPTIONS_NOTSELECTED_ICON("options_notselected_16x16.png"),
    SPLASH_IMAGE("fmgui_splash.png"),
    SHUTDOWN_IMAGE("fmgui_shutdown.png"),
    BAR_ICON("bar_16x16.png"),
    PIE_ICON("pie_16x16.png"),
    SETTING_ICON("setting_16x16.png"),
    FORWARD_WHITE_ICON("right-next_9x9.png"),
    BACK_WHITE_ICON("left-previous_9x9.png"),
    FORWARD_BLUE_ICON("right-next_12x12.png"),
    BACK_BLUE_ICON("left-previous_12x12.png"),
    EMPTY_BOX_ICON("emptyBox_16x16.png"),
    CHECK_BOX_ICON("checkBox_16x16.png"),
    CHECK_BLUE_ICON("check_blue_16x16.png"),
    CHECK_WHITE_ICON("check_white_16x16.png"),
    FOLDER_ICON("folder_16x16.png"),
    INFORMATION_ICON("information_16x16.png"),
    ZOOM_IN_ICON("zoom_in_16x16.png"),
    ZOOM_OUT_ICON("zoom_out_16x16.png"),
    FIT_WINDOW("fit_window_16x16.png"),
    EXPAND_ALL("expandAll_16x16.png"),
    COLLAPSE_ALL("collapseAll_16x16.png"),
    UNDO("left-previous_16x16.png"),
    REDO("right-next_16x16.png"),
    RESET("reset_16x16.png"),
    SLOW_LINK("slow_link_16x16.png"),
    NORMAL_LINK("normal_link_16x16.png"),
    INACTIVE_LINK("inactive_link_16x16.png"),
    RUNNING("running_16x16.gif"),
    DATA_TYPE("data_type_16x16.png"),
    REFRESH("refresh_16x16.png"),
    SHOW_BORDER("showBorder_16x16.png"),
    HIDE_BORDER("hideBorder_16x16.png"),
    ALT_ROWS("alternatingRows_16x16.png"),
    UNI_ROWS("uniformRows_16x16.png"),
    CLOSE_GRAY("close_gray_16x16.png"),
    CLOSE_WHITE("close_white_16x16.png"),
    CLOSE_RED("close_red_16x16.png"),
    LINK("link_16x16.png"),
    SWITCH_COLLAPSED_IMG("switch_plus_32x32.png"),
    SWITCH_EXPANDED_IMG("switch_32x32.png"),
    HFI_IMG("server_32x32.png"),
    CONSOLE_ICON("console_40x40.png"),
    APPS_LARGE_ICON("applications_40x40.png"),
    DEVICE_GROUP_LARGE_ICON("device_group_40x40.png"),
    VIRTUAL_FABRIC_LARGE_ICON("vf_40x40.png"),
    LINKS("links_16x16.png"),
    ROUTE("route_16x16.png"),
    DEVICE_SET("deviceSet_16x16.png"),
    CONNECT_GRAY("connect_gray_16x16.png"),
    CONNECT_WHITE("connect_white_16x16.png"),
    LOG_MENU_ICON("log_16x16.png"),
    LOG_ICON("log_40x40.png"),
    DIENAMIC_BAR("dienamic_bar-640x56.png"),
    EMAIL_ICON("email_16x16.png"),
    DISPLAY_ICON("message_16X16.png"),
    HISTORY_ICON("history_16x16.png"),
    UNEDITABLE("uneditable_16x16.png"),
    LINK_QUALITY_NONE("link_quality_16x16-0.png"),
    LINK_QUALITY_BAD("link_quality_16x16-1.png"),
    LINK_QUALITY_POOR("link_quality_16x16-2.png"),
    LINK_QUALITY_GOOD("link_quality_16x16-3.png"),
    LINK_QUALITY_VERY_GOOD("link_quality_16x16-4.png"),
    LINK_QUALITY_EXCELLENT("link_quality_16x16-5.png"),
    LINK_QUALITY_UNKNOWN("link_quality_16x16-error.png"),
    CHECK_MARK("checkmark_16x16.png"),
    X_MARK("x_mark_16x16.png"),
    DASH("dash_16x16.png"),
    PLAY("play_16x16.png"),
    PLAY_GRAY("play_gray_16x16.png"),
    STOP("stop_16x16.png"),
    STOP_RED("stop_red_16x16.png"),
    MOVE("move_16x16.png"),
    GO_UP("goUp_16x16.png"),
    GO_DOWN("goDown_16x16.png"),
    SYS_IMG("systemImage_16x16.png"),
    ABOUT_DIALOG_TOP_BANNER_IMG("AboutDlgTopBanner.png"),
    ABOUT_DIALOG_LEFT_BANNER_IMG("AboutDlgVerticalBanner.png"),
    INFO_DLG("info_32x32.png"),
    CONFIRM_DLG("confirm_32x32.png"),
    WARNING_DLG("warning_32x32.png"),
    ERROR_DLG("error_32x32.png"),
    SEARCH("search_16x16.png"),
    EXPAND_DOWN("expandDown_10x10.png"),
    EXPAND_UP("expandUp_10x10.png"),
    CABLE("cable_16x16.png"),
    INVISIBLE("invisible_16x16.png");

    private static final String STL_IMAGES_PATH = "/image/";

    private final String filename;

    private WeakReference<ImageIcon> icon;

    private WeakReference<Image> image;

    private UIImages(String filename) {
        this.filename = filename;
    }

    public ImageIcon getImageIcon() {
        if (icon == null || icon.get() == null) {
            URL loc = Util.class.getResource(STL_IMAGES_PATH + filename);
            if (loc == null) {
                return null;
            }
            ImageIcon realIcon = new ImageIcon(loc);
            icon = new WeakReference<ImageIcon>(realIcon);
        }
        return icon.get();
    }

    public Image getImage() {
        Image img = null;
        if (image == null || image.get() == null) {
            URL loc = Util.class.getResource(STL_IMAGES_PATH + filename);
            if (loc == null) {
                return null;
            }
            try {
                Image realImage = ImageIO.read(loc);
                image = new WeakReference<Image>(realImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (image != null) {
            img = image.get();
        }
        return img;
    }

    public String getFileName() {

        return STL_IMAGES_PATH + filename;
    }
}
