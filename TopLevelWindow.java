package main.java.resheto;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.util.*;


public class TopLevelWindow {


    protected JPanel mainPanel;
    protected ArrayList<OutputTextField> listOfFields;
    protected JFormattedTextField inputField;
    protected JFrame frame;
    protected Sieve sieve = null;

    public TopLevelWindow()
    {
        super();
        listOfFields = new ArrayList<OutputTextField>();
    }

    public void createWindow(int xpos, int ypos, int width, int height)
    {
        frame = new JFrame("Sieve of Erastophenes");

        //вся программа завершается, если какое-либо окно закрыто
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setPreferredSize(new Dimension(width, height));

        //Пользовательсикй класс
        WrapLayout wl = new WrapLayout();
        mainPanel = new JPanel(wl);

        //Вертикальная полоса прокрутки
        JScrollPane sp = new JScrollPane();
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setViewportBorder(BorderFactory.createLineBorder(Color.black));
        sp.getViewport().add(mainPanel);

        frame.getContentPane().add(sp, BorderLayout.CENTER);


        frame.setLocation(xpos, ypos);
        frame.pack();
        frame.setVisible(true);
    }

    public OutputTextField addTextField(String inStr, int width, int height)
    {
        //тесктовое поле с начальной строкой
        OutputTextField newText = addTextField(width, height);
        newText.updateText(inStr);
        refresh();
        return newText;
    }

    public OutputTextField addTextField(int width, int height)
    {
        //добавление текстового поля на панель
        OutputTextField newText = new OutputTextField(this);
        newText.setBorder(BorderFactory.createLineBorder(Color.black));
        newText.setPreferredSize(new Dimension(width, height));
        mainPanel.add(newText.getPane());
        listOfFields.add(newText);
        refresh();
        return newText;
    }
    //текстовая строка
    public JLabel addLabel(String inStr, int width, int height)
    {
        JLabel newLabel = new JLabel(inStr);
        newLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        newLabel.setPreferredSize(new Dimension(width, height));
        mainPanel.add(newLabel);
        refresh();
        return newLabel;
    }

    public void addFormattedTextField(String name, int width, int height, int defaultValue)
    {
        //Это поле ввода, принимает только цифры не меньше 3
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(3);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        inputField = new JFormattedTextField(formatter);
        inputField.setValue(defaultValue);
        inputField.setColumns(10);
        inputField.setBorder(BorderFactory.createLineBorder(Color.black));
        mainPanel.add(inputField);
        refresh();
    }

    //кнопка
    public JButton addButton(String name, int width, int height)
    {
        JButton newButton = new JButton(name);
        newButton.setPreferredSize(new Dimension(width, height));
        mainPanel.add(newButton);
        refresh();
        return newButton;
    }

    public int getFormattedTextFieldValue()
    {
        // Получить число, которое было введено
        Integer returnValue = (Integer)inputField.getValue();
        return returnValue.intValue();
    }

    public void refresh()
    {
        //Если обновление не вызывается в потоке, то оно не обновит графический интерфейс
        if (SwingUtilities.isEventDispatchThread())
        {
            callRefresh();
        } else {
            try {
                //Это запускает процесс для запуска в EDT, который обновит экран
                SwingUtilities.invokeAndWait(new Runnable() {public void run() {callRefresh();}});
            } catch (InvocationTargetException e) {
                // Это исключение может быть вызвано вводом действительно большого числа, а затем
                // щелчком внутри окна результатов, пока оно все еще записывает первые несколько чисел
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void callRefresh()
    {
        //повторная проверка
        mainPanel.revalidate();
    }

    public void clearLabels()
    {
        // Очистить содержимое всех меток
        for(OutputTextField otf : listOfFields)
        {
            otf.clearText();
        }
        refresh();
    }

    public void removeLabels()
    {
        mainPanel.removeAll();
        listOfFields.clear();
        refresh();
    }

    public ArrayList<OutputTextField> getFields()
    {
        return listOfFields;
    }

    public OutputTextField getFirstField()
    {
        if (listOfFields.size() > 0)
            return listOfFields.get(0);
        else
            return null;
    }

    public void setSieveRef(Sieve newSieve)
    {
        sieve = newSieve;
    }

    public void resetFlag()
    {
        if (sieve != null)
            sieve.resetFlag();
    }
}
