/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public
 * License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.erlide.ui.dialogs.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog field containing a label and a text control.
 */
public class StringDialogField extends DialogField {

    private String fText;

    private Text fTextControl;

    private ModifyListener fModifyListener;

    public StringDialogField() {
        super();
        fText = ""; //$NON-NLS-1$
    }

    // ------- layout helpers

    /*
     * @see DialogField#doFillIntoGrid
     */
    @Override
    public Control[] doFillIntoGrid(final Composite parent, final int nColumns) {
        assertEnoughColumns(nColumns);

        final Label label = getLabelControl(parent);
        label.setLayoutData(DialogField.gridDataForLabel(1));
        final Text text = getTextControl(parent);
        text.setLayoutData(StringDialogField.gridDataForText(nColumns - 1));

        return new Control[] { label, text };
    }

    /*
     * @see DialogField#getNumberOfControls
     */
    @Override
    public int getNumberOfControls() {
        return 2;
    }

    protected static GridData gridDataForText(final int span) {
        final GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = false;
        gd.horizontalSpan = span;
        return gd;
    }

    // ------- focus methods

    /*
     * @see DialogField#setFocus
     */
    @Override
    public boolean setFocus() {
        if (isOkToUse(fTextControl)) {
            fTextControl.setFocus();
            fTextControl.setSelection(0, fTextControl.getText().length());
        }
        return true;
    }

    // ------- ui creation

    /**
     * Creates or returns the created text control.
     *
     * @param parent
     *            The parent composite or <code>null</code> when the widget has already
     *            been created.
     */
    public Text getTextControl(final Composite parent) {
        if (fTextControl == null) {
            assertCompositeNotNull(parent);
            fModifyListener = e -> doModifyText(e);

            fTextControl = new Text(parent, SWT.SINGLE | SWT.BORDER);
            // moved up due to 1GEUNW2
            fTextControl.setText(fText);
            fTextControl.setFont(parent.getFont());
            fTextControl.addModifyListener(fModifyListener);

            fTextControl.setEnabled(isEnabled());
        }
        return fTextControl;
    }

    protected void doModifyText(final ModifyEvent e) {
        if (isOkToUse(fTextControl)) {
            fText = fTextControl.getText();
        }
        dialogFieldChanged();
    }

    // ------ enable / disable management

    /*
     * @see DialogField#updateEnableState
     */
    @Override
    protected void updateEnableState() {
        super.updateEnableState();
        if (isOkToUse(fTextControl)) {
            fTextControl.setEnabled(isEnabled());
        }
    }

    // ------ text access

    /**
     * Gets the text. Can not be <code>null</code>
     */
    public String getText() {
        return fText;
    }

    /**
     * Sets the text. Triggers a dialog-changed event.
     */
    public void setText(final String text) {
        fText = text;
        if (isOkToUse(fTextControl)) {
            fTextControl.setText(text);
        } else {
            dialogFieldChanged();
        }
    }

    /**
     * Sets the text without triggering a dialog-changed event.
     */
    public void setTextWithoutUpdate(final String text) {
        fText = text;
        if (isOkToUse(fTextControl)) {
            fTextControl.removeModifyListener(fModifyListener);
            fTextControl.setText(text);
            fTextControl.addModifyListener(fModifyListener);
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        if (isOkToUse(fTextControl)) {
            setTextWithoutUpdate(fText);
        }
    }

}
