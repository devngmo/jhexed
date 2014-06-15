/*
 * Copyright 2014 Igor Maznitsa.
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
package com.igormaznitsa.jhexed.swing.editor.ui.exporters;

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.misc.*;
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.renders.swing.ColorHexRender;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class ImageExporter implements Exporter {

  private final DocumentOptions docOptions;
  private final DialogSelectLayersForExport.SelectLayersExportData exportData;

  public ImageExporter(final DocumentOptions docOptions, final DialogSelectLayersForExport.SelectLayersExportData exportData) {
    this.docOptions = docOptions;
    this.exportData = exportData;
  }

  public BufferedImage generateImage() throws IOException {
    final int DEFAULT_CELL_WIDTH = 48;
    final int DEFAULT_CELL_HEIGHT = 48;

    final int imgWidth = this.docOptions.getImage() == null ? DEFAULT_CELL_WIDTH * this.docOptions.getColumns() : Math.round(this.docOptions.getImage().getSVGWidth());
    final int imgHeight = this.docOptions.getImage() == null ? DEFAULT_CELL_HEIGHT * this.docOptions.getRows() : Math.round(this.docOptions.getImage().getSVGHeight());

    final BufferedImage result;
    if (exportData.isBackgroundImageExport() && this.docOptions.getImage() != null) {
      result = this.docOptions.getImage().rasterize(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    }
    else {
      result = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    }

    final Graphics2D gfx = result.createGraphics();
    gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    final HexEngine<Graphics2D> engine = new HexEngine<Graphics2D>(DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT, this.docOptions.getHexOrientation());

    final HexFieldValue [] stackOfValues = new HexFieldValue[this.exportData.getLayers().size()];
    
    engine.setModel(new HexEngineModel<HexFieldValue[]>() {

      @Override
      public int getColumnNumber() {
        return docOptions.getColumns();
      }

      @Override
      public int getRowNumber() {
        return docOptions.getRows();
      }

      @Override
      public HexFieldValue[] getValueAt(final int col, final int row) {
        Arrays.fill(stackOfValues, null);
        
        int index = 0;
        for(final DialogSelectLayersForExport.LayerExportRecord r : exportData.getLayers()){
          if (r.isAllowed()){
            stackOfValues[index] = r.getLayer().getHexValueAtPos(col, row);
          }
          index ++;
        }
        return stackOfValues;
      }

      @Override
      public HexFieldValue[] getValueAt(final HexPosition pos) {
        return this.getValueAt(pos.getColumn(), pos.getRow());
      }

      @Override
      public void setValueAt(int col, int row, HexFieldValue[] value) {
      }

      @Override
      public void setValueAt(HexPosition pos, HexFieldValue[] value) {
      }

      @Override
      public boolean isPositionValid(final int col, final int row) {
        return col >= 0 && col < docOptions.getColumns() && row >= 0 && row < docOptions.getRows();
      }

      @Override
      public boolean isPositionValid(final HexPosition pos) {
        return this.isPositionValid(pos.getColumn(), pos.getRow());
      }

      @Override
      public void attachedToEngine(final HexEngine<?> engine) {
      }

      @Override
      public void detachedFromEngine(final HexEngine<?> engine) {
      }
    });

    final HexRect2D visibleSize = engine.getVisibleSize();
    final float xcoeff = (float) result.getWidth() / visibleSize.getWidth();
    final float ycoeff = (float) result.getHeight() / visibleSize.getHeight();
    engine.setScale(xcoeff, ycoeff);

    final Image[][] cachedIcons = new Image[this.exportData.getLayers().size()][];
    engine.setRenderer(new ColorHexRender() {

      private final Stroke stroke = new BasicStroke(docOptions.getLineWidth());

      @Override
      public Stroke getStroke() {
        return this.stroke;
      }

      @Override
      public Color getFillColor(HexEngineModel<?> model, int col, int row) {
        return null;
      }

      @Override
      public Color getBorderColor(HexEngineModel<?> model, int col, int row) {
        return docOptions.getColor();
      }

      @Override
      public void drawExtra(HexEngine<Graphics2D> engine, Graphics2D g, int col, int row, Color borderColor, Color fillColor) {
      }

      @Override
      public void drawUnderBorder(final HexEngine<Graphics2D> engine, final Graphics2D g, final int col, final int row, final Color borderColor, final Color fillColor) {
        final HexFieldValue [] stackValues = (HexFieldValue[])engine.getModel().getValueAt(col, row);
        for(int i=0;i<stackValues.length;i++){
          final HexFieldValue valueToDraw = stackValues[i];
          if (valueToDraw == null) continue;
          g.drawImage(cachedIcons[i][valueToDraw.getIndex()], 0, 0, null);
        }
      }

    });
    
    final Path2D hexShape = ((ColorHexRender)engine.getRenderer()).getHexPath();
    final int cellWidth = hexShape.getBounds().width;
    final int cellHeight = hexShape.getBounds().height;
    
    for (int i = 0; i<this.exportData.getLayers().size(); i++) {
      final DialogSelectLayersForExport.LayerExportRecord record = this.exportData.getLayers().get(i);
      if (record.isAllowed()) {
        final Image[] cacheLineForLayer = new Image[record.getLayer().getHexValuesNumber()];
        for (int v = 1; v < record.getLayer().getHexValuesNumber(); v++) {
          cacheLineForLayer[v] = record.getLayer().getHexValueForIndex(v).makeIcon(cellWidth, cellHeight, hexShape);
        }
        cachedIcons[i] = cacheLineForLayer;
      }
    }

    engine.draw(gfx);

    gfx.dispose();

    return result;
  }

  @Override
  public void export(final File file) throws IOException {
    final BufferedImage img = generateImage();
    ImageIO.write(img, "png", file);
  }
}
