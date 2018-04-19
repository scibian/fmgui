/**
 * Copyright (c) 2016, Intel Corporation
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

/**
 * This <b>enum</b> maintains all the UI widgets' names that will be very useful
 * for automatic GUI test. We use enum here because all the names need be
 * unique. On the GUI test script, a widget's name is exactly the same string we
 * show here.
 */
public enum WidgetName {
    //////////////////////////////////////
    // Setup Wizard, name starts with SW
    //////////////////////////////////////
    SW_SUBNETS,
    SW_ADD_SUBNET,
    SW_DELETE_SUBNET,
    SW_SUBNET_NAME,
    SW_APPLY,
    SW_RESET,
    SW_PREVIOUS,
    SW_NEXT,
    SW_RUN,
    SW_CLOSE,
    SW_HELP,
    SW_VAL_PROGRESS,
    SW_ENTRY_VALIDATION_STATUS,
    SW_ENTRY_VALIDATION_STATUS_ICON,
    SW_ENTRY_VALIDATION_NOTES,
    SW_HOST_REACHABILITY_STATUS,
    SW_HOST_REACHABILITY_STATUS_ICON,
    SW_HOST_REACHABILITY_NOTES,
    SW_DB_UPDATE_STATUS,
    SW_DB_UPDATE_STATUS_ICON,
    SW_DB_UPDATE_NOTES,
    SW_SUBNET_ERROR,
    // Setup Wizard->Hosts Tab, name starts with SW_H
    SW_H_AUTOCHECK,
    SW_H_ADD_HOST,
    // Host Info panel. NOTE, we may have multiple hosts, therefore multiple
    // panels. In our test script, need to use occurrence number to find a
    // specific host panel and its components
    SW_H_HOST_NAME,
    SW_H_HOST_PORT,
    SW_H_SECURE_CONN,
    SW_H_KEYSTORE_FILE_NAME,
    SW_H_KEYSTORE_FILE_BROWSER,
    SW_H_TRUSTSTORE_FILE_NAME,
    SW_H_TRUSTSTORE_FILE_BROWSER,
    SW_H_TEST_CONN,
    SW_H_CONN_STATUS,
    SW_H_REMOVE,
    // Setup Wizard->Events Tab, name starts with SW_E
    SW_E_TABBLE,
    SW_E_SEVERITY_OPTION,
    SW_E_SENT_EMAIL,
    SW_E_DISPLAT_MSG,
    // Setup Wizard->Preference Tab, name starts with SW_P
    SW_P_REFRESH_RATE,
    SW_P_REFRESH_RATE_UNIT,
    SW_P_TIME_WINDOW,
    SW_P_NUM_WORST_NODES,
    SW_P_EMAILS,
    SW_P_TEST_EMAILS,
    SW_P_EMAIL_TEST_NOTES,

    /////////////////////////////////////////////////
    // Logging Configuration, name starts with LOG_
    /////////////////////////////////////////////////
    LOG_LEVEL,
    LOG_OUTPUT_FORMAT,
    LOG_OUTPUT_FORMAT_HELP,
    LOG_FILE_SIZE,
    LOG_FILE_SIZE_UNIT,
    LOG_MAX_NUM_FILES,
    LOG_FILE_LOCATION,
    LOG_FILE_BROWSER,
    LOG_RESET,
    LOG_OK,
    LOG_CANCEL,
    LOG_PREVIW_TEXT,
    LOG_PREVIW,
    LOG_PREVIW_OUTPUT,
    LOG_PREVIW_OK,
    LOG_PREVIW_CANCEL,

    //////////////////////////////////////////
    // About Dialog, name starts with ABOUT_
    //////////////////////////////////////////
    ABOUT_PRODUCT_NAME_VERSION,
    ABOUT_BUILD_DATE,
    ABOUT_OK,

    //////////////////////////////////////
    // Email Setup, name starts with ES
    //////////////////////////////////////
    ES_ENABLE_NOTIFICATION,
    ES_SMTP_HOST,
    ES_SMTP_PORT,
    ES_SENDER,
    ES_RECEIVERS,
    ES_TEST,
    ES_OK,
    ES_CANCEL,
    ES_RESET,

    ////////////////////////////////////
    // Home Page, name starts with HP_
    ////////////////////////////////////
    // Statistic card, name starts with HP_STA_
    HP_STA_TITLE,
    HP_STA_MASTER_SM,
    HP_STA_MSM_UPTIME,
    HP_STA_STANDBY_SMS,
    HP_STA_ISL, // Internal Switch Links
    HP_STA_HOST_LINKS,
    HP_STA_OTHER_PORTS,
    HP_STA_NODES_SUM, // nodes summary card
    HP_STA_PORTS_SUM, // ports summary card
    HP_STA_HELP,
    HP_STA_PIN,
    // summary card, name starts with HP_STA_SUM_
    HP_STA_SUM_TOTAL,
    HP_STA_SUM_NAME,
    HP_STA_SUM_FAILED_CHART,
    HP_STA_SUM_FAILED_NUM,
    HP_STA_SUM_SKIPPED_CHART,
    HP_STA_SUM_SKIPPED_NUM,
    HP_STA_SUM_TYPE_CHART,
    // the following is the generic name for each type. The real widget name
    // will be HP_STA_SUM_TYPE_NUM_?, HP_STA_SUM_TYPE_NAME_? where '?' is the
    // type index. For now, it will be HP_STA_SUM_TYPE_NUM_1 for Switches,
    // HP_STA_SUM_TYPE_NUM_2 for HFIs.
    HP_STA_SUM_TYPE_NUM_, // number for one type
    HP_STA_SUM_TYPE_NAME_, // name of one type

    // Status card, name starts with HP_STU_
    HP_STU_STYLE,
    HP_STU_SW_DIST,
    HP_STU_HFI_DIST,
    // the following is the generic name for each status. The real widget name
    // will be HP_STU_DIST_BAR_? where '?' is the status index. For now, it will
    // be HP_STU_DIST_BAR_1 for Critical, HP_STU_DIST_BAR_2 for Error etc.
    HP_STU_DIST_BAR_, // for bar chart
    HP_STU_DIST_CHART, // for pie chart
    HP_STU_DIST_LABEL_, // for bar and pie chart
    HP_STU_HELP,
    HP_STU_PIN,
    HP_STU_CHART_STYLE,

    // The following are the names for widgets in Health Trend Card
    HP_HEALTH_HIST_HELP,
    HP_HEALTH_HIST_PIN,

    // The following are the names for widgets in Worst Nodes Card
    HP_WORST_NODES_HELP,
    HP_WORST_NODES_PIN,

    // Widget names for the top level subnet summary and subnet performance
    // panel.
    HP_STAT_SUM_SECTION_HELP,
    HP_PERF_SUM_SECTION_HELP,

    // Generic widget and panel names for the Cards in the Home Page subnet
    // performance section
    // Real name will be HP_PERF_TREND_HELP_? where '?' is the section type.
    // Section types are
    // BW - Bandwidth, Packet Rate, Congestion, SMA Congestion, Integrity,
    // Bubble, Security, Routing
    HP_PERF_TREND_HELP_,
    HP_PERF_TREND_PIN_,
    HP_PERF_TREND_PANEL_,

    HP_PERF_TOPN_HISTOGRAM_HELP_,
    HP_PERF_TOPN_HISTOGRAM_PIN_,
    HP_PERF_TOPN_HISTOGRAM_PANEL_,

    // Generic widget and panel names for the Cards in the Performance Page
    // performance section. Real name will be PP_PERF_TREND_HELP_? where '?' is
    // the section type.
    PP_PERF_TREND_HELP_,
    PP_PERF_TREND_PIN_,
    PP_PERF_TREND_PANEL_,

    PP_PERF_TOPN_HELP_,
    PP_PERF_TOPN_PIN_,
    PP_PERF_TOPN_PANEL_,

    PP_PERF_HISTOGRAM_HELP_,
    PP_PERF_HISTOGRAM_PIN_,
    PP_PERF_HISTOGRAM_PANEL_,

    /////////////////////////////////////////
    // Admin Page. There are five sub pages.
    // Applications: ADMIN_APP_
    // Device Groups: ADMIN_DG_
    // Virtual Fabrics: ADMIN_VF_
    // Console: ADMIN_CONSOLE_
    // Logs: ADMIN_LOGS_
    // Names are listed from top-left (check boxes in Filters section), to
    // bottom-right (Line Range values) of the UI.
    /////////////////////////////////////////
    ADMIN_DG_MOVE,

    // DEPLOY is for the deploy page within the Virtual Fabrics section.
    // This is for the unlabeled check box left of the "Host:" label.
    ADMIN_VF_DEPLOY_SELECT_BOX,
    ADMIN_VF_DEPLOY_HOST,
    ADMIN_VF_DEPLOY_PORT_NUMBER,
    ADMIN_VF_DEPLOY_USER_NAME,
    ADMIN_VF_DEPLOY_PASSWORD,
    // State icon and state message don't appear in default.
    ADMIN_VF_DEPLOY_STATE_ICON,
    ADMIN_VF_DEPLOY_STATE_MESSAGE,
    // This is for the check box labeled as "Apply user name and password on
    // standby SMs".
    ADMIN_VF_DEPLOY_SAME_CREDENTIAL,
    ADMIN_VF_DEPLOY_STANDBY_SMS,
    ADMIN_VF_DEPLOY_ADD_NEW_STANDBY_SM,
    ADMIN_VF_DEPLOY_DEPLOY,
    ADMIN_VF_DEPLOY_BACK,

    ADMIN_CONSOLE_LOGIN_USERNAME,
    ADMIN_CONSOLE_LOGIN_PASSWORD,
    ADMIN_CONSOLE_LOGIN_HOST,
    ADMIN_CONSOLE_LOGIN_PORT,
    ADMIN_CONSOLE_LOGIN_BUTTON,
    ADMIN_CONSOLE_CANCEL_BUTTON,
    // The "running" icon that appears while waiting for a log in.
    ADMIN_CONSOLE_LOGIN_STATUS_ICON,
    ADMIN_CONSOLE_LOGIN_TEXT_AREA,
    ADMIN_CONSOLE_TERMINAL_HOST,
    ADMIN_CONSOLE_TERMINAL_PORT,
    ADMIN_CONSOLE_TERMINAL_USERNAME,
    ADMIN_CONSOLE_LOCK,
    ADMIN_CONSOLE_SEND,
    ADMIN_CONSOLE_COMMAND_BOX,
    ADMIN_CONSOLE_CURRENT_TAB,
    ADMIN_CONSOLE_NEW_TAB,

    ADMIN_LOGS_FILTERS_SM,
    ADMIN_LOGS_FILTERS_PM,
    ADMIN_LOGS_FILTERS_FE,
    ADMIN_LOGS_FILTERS_WARN,
    ADMIN_LOGS_FILTERS_ERROR,

    // LPP stands for Lines Per Page.
    ADMIN_LOGS_LPP_LABEL,
    ADMIN_LOGS_LPP_TEXT_FIELD,
    ADMIN_LOGS_LPP_COMBO_BOX,
    ADMIN_LOGS_LPP_REFRESH_BUTTON,
    // When refresh button is clicked, cycling label appears on the right.
    ADMIN_LOGS_LPP_REFRESH_RUNNING,

    ADMIN_LOGS_SEARCH_TEXT_FIELD,
    ADMIN_LOGS_SEARCH_BUTTON,
    ADMIN_LOGS_SEARCH_CANCEL_BUTTON,

    ADMIN_LOGS_PREVIOUS_BUTTON,
    ADMIN_LOGS_NEXT_BUTTON,
    // When previous or next button is clicked, cycling label appears on the
    // right.
    ADMIN_LOGS_PAGE_RUNNING,
    ADMIN_LOGS_CONFIGURE_BUTTON,
    ADMIN_LOGS_HELP_BUTTON,

    ADMIN_LOGS_FILE_NAME_LABEL,
    ADMIN_LOGS_FILE_NAME_VALUE,

    ADMIN_LOGS_TOTAL_LINES_LABEL,
    ADMIN_LOGS_TOTAL_LINES_VALUE,

    ADMIN_LOGS_NUM_MATCHES_LABEL,
    ADMIN_LOGS_NUM_MATCHES_VALUE,

    ADMIN_LOGS_LINE_RANGE_LABEL,
    ADMIN_LOGS_LINE_RANGE_START,
    ADMIN_LOGS_LINE_RANGE_END,

    /////////////////////////////////////////
    // Performance Page. Name starts with PM_
    /////////////////////////////////////////
    // Properties Tab. Name starts with PM_PROP_

    // Generic name. After the last underscore is the actual name, such as group
    // name, category name etc.
    PM_PROP_GROUP_,
    PM_PROP_CATEGORY_,
    PM_PROP_HEADER_NAME_,
    PM_PROP_HEADER_VALUE_,
    PM_PROP_ITEM_NAME_,
    PM_PROP_ITEM_VALUE_,

    ////////////////////////////////////////
    // Common widgets, name starts with COM_
    ////////////////////////////////////////
    COM_DURATION_DAYS,
    COM_DURATION_HOURS,
    COM_DURATION_MINUTES;
}
