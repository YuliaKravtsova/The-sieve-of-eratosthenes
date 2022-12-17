package main.java.resheto;

import java.io.PrintWriter;
import java.lang.Runnable;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.FileNotFoundException;

public class Sifter<time> implements Runnable {
    protected int siftValue; //значение фильтра
    protected static int maxValue; //макисмальное значение
    protected Sifter nextSifter = null; //след фильтр
    protected OutputTextField siftLabel;
    protected OutputTextField resultsText; //результат
    protected static TopLevelWindow workingWindow = null; //рабочее очкно

    //блокирующая очередь
    protected BlockingQueue<Integer> receiveQueue = new LinkedBlockingQueue<Integer>();
    protected BlockingQueue<Integer> sendQueue = null;

    public Sifter() {
    }

    public Sifter(int inValue) {
        siftValue = inValue;
    }

    public void run() {
        Instant start = Instant.now();
//при передаче ошибки, т.е. значения -1
        Integer sentValue;
        int sentInt;
        try {
            boolean loopFlag = true;
            while (loopFlag) {
                sentValue = receiveQueue.take();
                sentInt = sentValue.intValue();
                if (sentInt == -1) {
                    loopFlag = false;
                    if (sendQueue != null) {
                        sendQueue.put(sentValue);
                    }
                } else {
                    processNumber(sentValue.intValue());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        siftLabel.updateText("Finished");
        if (nextSifter == null) {
//Просеивание завершено, сьрасывакм флаг, чтобы разрешить следующее выполнение программы
            workingWindow.resetFlag();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
    }

    public BlockingQueue<Integer> getSendQueue() {
        return sendQueue;
    }

    public void setReceiveQueue(BlockingQueue<Integer> inQueue) {
        receiveQueue = inQueue;
    }

    public void setSiftValues(int inValue) {
        siftValue = inValue;
    }

    public void setMaxValue(int inValue) {
        double dMaxValue = Math.sqrt(inValue);
        maxValue = (int) dMaxValue;
    }

    public void setSiftLabel(OutputTextField inLabel) {
        siftLabel = inLabel;
    }

    public void setResultsText(OutputTextField inText) {
        resultsText = inText;
    }

    public void setWorkingWindow(TopLevelWindow inWorkingWindow) {
        workingWindow = inWorkingWindow;
    }

    //Процесс нахождения кратных
    public void processNumber(int inValue) {
        StringBuilder newText = new StringBuilder();
        newText.append(inValue);
        if (inValue % siftValue == 0) {
            newText.append(" - Dropped");
        }
        else {
            if (nextSifter != null) {
                try {
                    sendQueue.put(new Integer(inValue));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                newText.append(" - Passed");//передано
            } else {
                resultsText.updateText(inValue + " found"); //результат
                if (inValue <= maxValue) {
                    nextSifter = new Sifter(inValue);
                    nextSifter.setSiftValues(inValue);
                    nextSifter.setSiftLabel(workingWindow.addTextField("Starting with filter " + inValue, 300, 100));
                    nextSifter.setResultsText(resultsText);
                    sendQueue = new LinkedBlockingQueue<Integer>();
                    nextSifter.setReceiveQueue(sendQueue);
                    new Thread(nextSifter).start();
                    newText.append(" - New Sifter created");
                } else {
                    newText.append(" - Prime");
                }
            }
        }

        siftLabel.updateText(newText.toString());
        workingWindow.refresh();
    }
}