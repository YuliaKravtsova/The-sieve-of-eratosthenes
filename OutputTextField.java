package main.java.resheto;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.TimeUnit;

public class OutputTextField{

    private JTextArea jtArea; //текстовая область
    private JScrollPane jsPane; //панель прокрутки
    private boolean locked = false; //блок
    private int prevMaxAdjustment = 0;

    public OutputTextField(TopLevelWindow inWindow) //поле вывода
    {
        jtArea = new JTextArea();

        //прокрутка
        jsPane = new JScrollPane(jtArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar verticalBar = jsPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                //Если максимальный размер полосы прокрутки изменился, перейдите к максимальному
                // (последнему) элементу
                if (adjustable.getMaximum() != prevMaxAdjustment)
                {
                    prevMaxAdjustment = adjustable.getMaximum();
                    adjustable.setValue(prevMaxAdjustment);

                }
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }

    public JScrollPane getPane()
    {
        return jsPane;
    } // вернуть панель прокрутки

    public void updateText(String inText)
    {
        // This can be access from multiple threads
        while (locked == true)
        {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        locked = true;

        // Добавление новой строки, если в текстовой области уже есть текст
        if (jtArea.getText() != null && !(jtArea.getText().equals(""))) {
            jtArea.append(System.lineSeparator());
        }
        jtArea.append(inText);

        locked = false;
    }

    // Расширение JPane
    public void setBorder(Border inBorder)
    {
        jsPane.setBorder(inBorder);
    }

    public void setPreferredSize(Dimension inDimension)
    {
        jsPane.setPreferredSize(inDimension);
    }

    public void clearText()
    {
        jtArea.setText("");
    }

    public void refresh()
    {
        jsPane.repaint();
    }
}
