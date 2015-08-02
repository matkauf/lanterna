/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * This class containers a couple of border implementation
 */
public class Borders {
    private Borders() {
    }

    //Different ways to draw the border
    private enum BorderStyle {
        Solid,
        Bevel,
        ReverseBevel,
    }

    public static Border singleLine() {
        return singleLine("");
    }

    public static Border singleLine(String title) {
        return new SingleLine(title, BorderStyle.Solid);
    }

    public static Border singleLineBevel() {
        return singleLineBevel("");
    }

    public static Border singleLineBevel(String title) {
        return new SingleLine(title, BorderStyle.Bevel);
    }

    public static Border singleLineReverseBevel() {
        return singleLineReverseBevel("");
    }

    public static Border singleLineReverseBevel(String title) {
        return new SingleLine(title, BorderStyle.ReverseBevel);
    }

    public static Border doubleLine() {
        return doubleLine("");
    }

    public static Border doubleLine(String title) {
        return new DoubleLine(title, BorderStyle.Solid);
    }

    public static Border doubleLineBevel() {
        return doubleLineBevel("");
    }

    public static Border doubleLineBevel(String title) {
        return new DoubleLine(title, BorderStyle.Bevel);
    }

    public static Border doubleLineReverseBevel() {
        return doubleLineReverseBevel("");
    }

    public static Border doubleLineReverseBevel(String title) {
        return new DoubleLine(title, BorderStyle.ReverseBevel);
    }

    private static abstract class StandardBorder extends AbstractBorder {
        private final String title;

        protected StandardBorder(String title) {
            if (title == null) {
                throw new IllegalArgumentException("Cannot create a border with null title");
            }
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + title + "}";
        }
    }

    private static abstract class AbstractBorderRenderer implements Border.BorderRenderer {
        private final BorderStyle borderStyle;

        protected AbstractBorderRenderer(BorderStyle borderStyle) {
            this.borderStyle = borderStyle;
        }

        @Override
        public TerminalSize getPreferredSize(Border component) {
            StandardBorder border = (StandardBorder)component;
            Component wrappedComponent = border.getComponent();
            TerminalSize preferredSize;
            if (wrappedComponent == null) {
                preferredSize = TerminalSize.ZERO;
            } else {
                preferredSize = wrappedComponent.getPreferredSize();
            }
            preferredSize = preferredSize.withRelativeColumns(2).withRelativeRows(2);
            String borderTitle = border.getTitle();
            return preferredSize.max(new TerminalSize((borderTitle.isEmpty() ? 2 : borderTitle.length() + 4), 2));
        }

        @Override
        public TerminalPosition getWrappedComponentTopLeftOffset() {
            return TerminalPosition.OFFSET_1x1;
        }

        @Override
        public TerminalSize getWrappedComponentSize(TerminalSize borderSize) {
            return borderSize
                    .withRelativeColumns(-Math.min(2, borderSize.getColumns()))
                    .withRelativeRows(-Math.min(2, borderSize.getRows()));
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Border component) {
            StandardBorder border = (StandardBorder)component;
            Component wrappedComponent = border.getComponent();
            if(wrappedComponent == null) {
                return;
            }
            TerminalSize drawableArea = graphics.getSize();

            char horizontalLine = getHorizontalLine(graphics);
            char verticalLine = getVerticalLine(graphics);
            char bottomLeftCorner = getBottomLeftCorner(graphics);
            char topLeftCorner = getTopLeftCorner(graphics);
            char bottomRightCorner = getBottomRightCorner(graphics);
            char topRightCorner = getTopRightCorner(graphics);

            if(borderStyle == BorderStyle.Bevel) {
                graphics.applyThemeStyle(graphics.getThemeDefinition(StandardBorder.class).getPreLight());
            }
            else {
                graphics.applyThemeStyle(graphics.getThemeDefinition(StandardBorder.class).getNormal());
            }
            graphics.setCharacter(0, drawableArea.getRows() - 1, bottomLeftCorner);
            if(drawableArea.getRows() > 2) {
                graphics.drawLine(new TerminalPosition(0, drawableArea.getRows() - 2), new TerminalPosition(0, 1), verticalLine);
            }
            graphics.setCharacter(0, 0, topLeftCorner);
            if(drawableArea.getColumns() > 2) {
                graphics.drawLine(new TerminalPosition(1, 0), new TerminalPosition(drawableArea.getColumns() - 2, 0), horizontalLine);
            }

            if(borderStyle == BorderStyle.ReverseBevel) {
                graphics.applyThemeStyle(graphics.getThemeDefinition(StandardBorder.class).getPreLight());
            }
            else {
                graphics.applyThemeStyle(graphics.getThemeDefinition(StandardBorder.class).getNormal());
            }
            graphics.setCharacter(drawableArea.getColumns() - 1, 0, topRightCorner);
            if(drawableArea.getRows() > 2) {
                graphics.drawLine(new TerminalPosition(drawableArea.getColumns() - 1, 1),
                        new TerminalPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 2),
                        verticalLine);
            }
            graphics.setCharacter(drawableArea.getColumns() - 1, drawableArea.getRows() - 1, bottomRightCorner);
            if(drawableArea.getColumns() > 2) {
                graphics.drawLine(new TerminalPosition(1, drawableArea.getRows() - 1),
                        new TerminalPosition(drawableArea.getColumns() - 2, drawableArea.getRows() - 1),
                        horizontalLine);
            }

            if(drawableArea.getColumns() >= border.getTitle().length() + 4) {
                graphics.putString(2, 0, border.getTitle());
            }

            wrappedComponent.draw(graphics.newTextGraphics(getWrappedComponentTopLeftOffset(), getWrappedComponentSize(drawableArea)));
        }

        protected abstract char getHorizontalLine(TextGUIGraphics graphics);
        protected abstract char getVerticalLine(TextGUIGraphics graphics);
        protected abstract char getBottomLeftCorner(TextGUIGraphics graphics);
        protected abstract char getTopLeftCorner(TextGUIGraphics graphics);
        protected abstract char getBottomRightCorner(TextGUIGraphics graphics);
        protected abstract char getTopRightCorner(TextGUIGraphics graphics);
    }

    private static class SingleLine extends StandardBorder {
        private final BorderStyle borderStyle;

        private SingleLine(String title, BorderStyle borderStyle) {
            super(title);
            this.borderStyle = borderStyle;
        }

        @Override
        protected BorderRenderer createDefaultRenderer() {
            return new SingleLineRenderer(borderStyle);
        }
    }

    private static class SingleLineRenderer extends AbstractBorderRenderer {
        public SingleLineRenderer(BorderStyle borderStyle) {
            super(borderStyle);
        }

        @Override
        protected char getTopRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLineRenderer.class).getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
        }

        @Override
        protected char getBottomRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLineRenderer.class).getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        }

        @Override
        protected char getTopLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLineRenderer.class).getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
        }

        @Override
        protected char getBottomLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLineRenderer.class).getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        }

        @Override
        protected char getVerticalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLineRenderer.class).getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL);
        }

        @Override
        protected char getHorizontalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLineRenderer.class).getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL);
        }
    }

    private static class DoubleLine extends StandardBorder {
        private final BorderStyle borderStyle;

        private DoubleLine(String title, BorderStyle borderStyle) {
            super(title);
            this.borderStyle = borderStyle;
        }

        @Override
        protected BorderRenderer createDefaultRenderer() {
            return new DoubleLineRenderer(borderStyle);
        }
    }

    private static class DoubleLineRenderer extends AbstractBorderRenderer {
        public DoubleLineRenderer(BorderStyle borderStyle) {
            super(borderStyle);
        }

        @Override
        protected char getTopRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("TOP_RIGHT_CORNER", Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        }

        @Override
        protected char getBottomRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("BOTTOM_RIGHT_CORNER", Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
        }

        @Override
        protected char getTopLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("TOP_LEFT_CORNER", Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        }

        @Override
        protected char getBottomLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("BOTTOM_LEFT_CORNER", Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        }

        @Override
        protected char getVerticalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("VERTICAL_LINE", Symbols.DOUBLE_LINE_VERTICAL);
        }

        @Override
        protected char getHorizontalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("HORIZONTAL_LINE", Symbols.DOUBLE_LINE_HORIZONTAL);
        }
    }
}
