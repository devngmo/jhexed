/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jhexed.swing.editor.ui.frames.layers;

import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.extapp.hexes.HexLayer;
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.*;
import com.igormaznitsa.jhexed.swing.editor.ui.Utils;
import static com.igormaznitsa.jhexed.swing.editor.ui.Utils.*;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.*;

public class LayerRecordPanel extends javax.swing.JPanel implements HexLayer{
  private static final long serialVersionUID = -6730121049908309123L;

  private final HexFieldLayer layer;
  private final LayerListModel parent;
  
  public LayerRecordPanel(final LayerListModel parent, final HexFieldLayer layer) {
    initComponents();
    this.layer = layer;
    this.parent = parent;
    refreshView();
  }

  public void refreshView(){
    this.labelLayerName.setText(this.layer.getLayerName());
    this.checkBoxVisibility.setSelected(this.layer.isLayerVisible());
    revalidate();
    repaint();
  }

  @Override
  public HexFieldLayer getHexField() {
    return this.layer;
  }

  public void updateLayer(final HexFieldLayer data){
    this.layer.loadFromAnotherInstance(data);
    refreshView();
    this.parent.changedItem(this);
  }
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    labelLayerName = new javax.swing.JLabel();
    checkBoxVisibility = new javax.swing.JCheckBox();

    checkBoxVisibility.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkBoxVisibilityStateChanged(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(checkBoxVisibility)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(labelLayerName, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(labelLayerName, javax.swing.GroupLayout.Alignment.TRAILING)
      .addComponent(checkBoxVisibility, javax.swing.GroupLayout.Alignment.TRAILING)
    );

    layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {checkBoxVisibility, labelLayerName});

  }// </editor-fold>//GEN-END:initComponents

  private void checkBoxVisibilityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkBoxVisibilityStateChanged
    this.layer.setVisible(this.checkBoxVisibility.isSelected());
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.HEX_FIELD_NEEDS_REPAINT, this.layer);
  }//GEN-LAST:event_checkBoxVisibilityStateChanged


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkBoxVisibility;
  private javax.swing.JLabel labelLayerName;
  // End of variables declaration//GEN-END:variables

  public void setSelected(final boolean selected) {
    this.setBackground(Utils.getListBackground(selected));
    
    final Color fg = getListForeground(selected);
    
    for(final Component c : this.getComponents()){
      c.setForeground(fg);
    }
  }

  public LayerListModel getLayerListModel() {
    return this.parent;
  }
  
  @Override
  public String toString(){
    return this.layer.getLayerName();
  }

  @Override
  public int getValue(final HexPosition pos) {
    return this.layer.getHexValueAtPos(pos.getColumn(), pos.getRow()).getIndex();
  }

  @Override
  public void setValue(final HexPosition pos, final int value) {
    if(this.layer.isPositionValid(pos))
    this.layer.setValueAtPos(pos.getColumn(), pos.getRow(),value);
  }

  @Override
  public HexFieldValue findHexViewValueForIndex(final int value) {
    return this.layer.getHexValueForIndex(value);
  }

  @Override
  public int getMaxValue() {
    return this.layer.getHexValueForIndex(this.layer.getHexValuesNumber()-1).getIndex();
  }

}
