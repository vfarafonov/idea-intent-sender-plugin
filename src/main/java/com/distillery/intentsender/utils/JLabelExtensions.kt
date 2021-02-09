package com.distillery.intentsender.utils

import javax.swing.Icon
import javax.swing.JLabel

/** Displays error icon and sets tooltip text. */
fun JLabel.showError(icon: Icon, message: String) {
    this.icon = icon
    toolTipText = message;
}

/** Hides error icon and removes tooltip. */
fun JLabel.hideError() {
    icon = null;
    toolTipText = null;
}
