/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cuccovillo.alessio.pdfsplitmerge.gui.component;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Alessio.Cuccovillo
 */
public class SlideUpDownText extends JComponent {

    private String text;
    private Point startPosition;
    private Point currentPosition;
    private int fullHeight;
    private boolean slideUp;
    private boolean started;
    private int speed;

    public SlideUpDownText() {
        text = "";
        slideUp = true;
        started = false;
        speed = 100;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
    }

    public boolean isSlideUp() {
        return slideUp;
    }

    public void setSlideUp(boolean slideUp) {
        this.slideUp = slideUp;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (started) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(getForeground());
            if (startPosition == null) {
                preparePositions(g2d.getFontMetrics());
            }
            if (currentPosition.y == 0) {
                currentPosition.y = startPosition.y;
            }
            int step = g2d.getFontMetrics().getHeight();
            int y = currentPosition.y;
            if (slideUp) {
                currentPosition.y -= 1;
                step *= -1;
            } else {
                currentPosition.y += 1;
            }
            List<String> lines = prepareLines();
            for (String line : lines) {
                y += step;
                if (y < fullHeight) {
                    g2d.drawString(line, currentPosition.x, y);
                } else {
                    break;
                }
            }
        }
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
        }
        repaint();
    }

    private void preparePositions(FontMetrics fm) {
        fullHeight = getHeight() + getTextHeight(fm) + fm.getHeight() / 4;
        startPosition = new Point(0, fullHeight);
        currentPosition = new Point(0, fullHeight);
    }

    private int getTextHeight(FontMetrics fm) {
        return fm.getHeight() * text.split("\\n").length;
    }

    private List<String> prepareLines() {
        String[] lines = text.split("\n");
        List<String> result = new ArrayList<>(lines.length);
        int index = 0;
        int step = 1;
        if (slideUp) {
            index = lines.length - 1;
            step *= -1;
        }
        try {
            for (;;) {
                result.add(lines[index]);
                index += step;
            }
        } catch (IndexOutOfBoundsException ioobe) {
            // do nothing
        }
        return result;
    }
}
