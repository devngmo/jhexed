package com.igormaznitsa.jhexed.swing.editor.ui.frames;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.*;
import java.awt.event.*;
import java.util.Enumeration;
import javax.swing.*;
import org.jdesktop.swingx.WrapLayout;

public class FrameTools extends javax.swing.JInternalFrame implements InsideApplicationBus.AppBusListener, ActionListener {

  private static final long serialVersionUID = -2106015366224156744L;

  public FrameTools() {
    initComponents();

    InsideApplicationBus.getInstance().addAppBusListener(this);

    final JPanel buttonPanel = new JPanel(new WrapLayout(WrapLayout.LEFT));
    
    for (final ToolType t : ToolType.values()) {
      final ToolButton button = new ToolButton(t);
      button.setEnabled(false);
      button.setIcon(t.getIcon());
      button.setToolTipText(t.getDescription());
      button.addActionListener(this);
      this.toolsButtonGroup.add(button);
      buttonPanel.add(button);
    }

    this.setContentPane(buttonPanel);
    
    pack();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    toolsButtonGroup = new javax.swing.ButtonGroup();

    setClosable(true);
    setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    setResizable(true);
    setTitle("Tools");
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentHidden(java.awt.event.ComponentEvent evt) {
        formComponentHidden(evt);
      }
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.A_FRAME_CHANGED_ITS_STATUS, this, FrameType.TOOLS);
  }//GEN-LAST:event_formComponentHidden

  private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.A_FRAME_CHANGED_ITS_STATUS, this, FrameType.TOOLS);
  }//GEN-LAST:event_formComponentShown


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup toolsButtonGroup;
  // End of variables declaration//GEN-END:variables

  @Override
  public void setVisible(final boolean flag) {
    super.setVisible(flag);
    if (flag) {
      InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.REQUEST_EVENT, InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED);
    }
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    final ToolButton btn = (ToolButton) e.getSource();
    if (btn.isSelected()) {
      InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.SELECTED_TOOL_CHANGED, btn.getType());
      InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.REQUEST_EVENT, InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED);
    }
  }

  private void changeButtonsState(final boolean enabled) {
    for (final Enumeration<AbstractButton> e = this.toolsButtonGroup.getElements(); e.hasMoreElements();) {
      final ToolButton nxtbutton = (ToolButton) e.nextElement();
      if (nxtbutton.isSelected()) {
        nxtbutton.setSelected(false);
      }
      nxtbutton.setEnabled(enabled);
    }
  }

  @Override
  public void onAppBusEvent(final Object source, final InsideApplicationBus bus, final InsideApplicationBus.AppBusEvent event, final Object... objects) {
    if (event == InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED) {
      this.toolsButtonGroup.clearSelection();
      final HexFieldLayer layer = (HexFieldLayer) objects[0];
      if (layer == null) {
        changeButtonsState(false);
        InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.SELECTED_TOOL_CHANGED, (Object) null);
      }
      else {
        changeButtonsState(true);
      }
    }
  }

}
