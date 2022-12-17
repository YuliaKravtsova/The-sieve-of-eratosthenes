package main.java.resheto;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingWorker;

public class Sieve {

    protected TopLevelWindow resultsWindow; //результат
    protected TopLevelWindow inputWindow; //ввод
    protected TopLevelWindow workingWindow; //рабочее окно
    protected Feeder feeder;
    protected Keeper keeper = new Keeper();
    protected boolean running = false;
    JButton startButton; //кнопка запуска

    public static void main(String[] args) { //запуск
        Sieve mainSieve = new Sieve();
        mainSieve.StartUp();
    }

    public void StartUp()
    {
// Make some windows and add an input field for numbers only, plus a button
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height; // набор инстурментов по умолчанию

//параметры для окна с результатом
        resultsWindow = new TopLevelWindow();
        resultsWindow.createWindow(screenWidth - 350, 300, 350, 300);
        resultsWindow.addLabel("Results", 300, 100);

//параметры для окна с вводом
        inputWindow = new TopLevelWindow();
        inputWindow.createWindow(screenWidth - 300, 100, 200, 150);
        inputWindow.addLabel("Input a number", 150, 30);
        inputWindow.addFormattedTextField("Input", 300, 100, 0);
        startButton = inputWindow.addButton("Press me!", 150, 30);

//Кнопка должна вызывать метод actionPerformed класса типа ActionListener
// Итак, мы просто создадим анонимный класс и привяжем его к прослушивателю действий кнопки
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setText("Running...");
                StartBackgroundSieving();
            }
        });
        //рабочее окно параметры
        workingWindow = new TopLevelWindow();
        workingWindow.createWindow(200, 100, 1000, screenHeight - 200);

        workingWindow.setSieveRef(this);
    }
    //очистка входных данных
    public void ClearOut()
    {

        resultsWindow.clearLabels();
        workingWindow.removeLabels();
    }

    public void StartBackgroundSieving()
    {
        if (running) return;

        running = true;
// We don't want this starting up whilst an existing job is running

// We need to get this processing off the Event Dispatcher Thread.
// Unfortunately starting a background thread normally doesn't do this,
// (starting a thread from the EDT means the new thread is also counted as being the EDT)
// so we shall make use of the SwingWorker class which will sort it out for us.
// There are other ways of creating a background thread, but if there is a library
// that does it for us it makes sense to use it.
// No input and no return needed, so the SwingWorker types are Void and Void.
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                ClearOut();
                StartSieving();
                return null;
            }
        };
        worker.execute();
    }

    public void StartSieving()
    {
// получаем максимальное значение
        int value = inputWindow.getFormattedTextFieldValue();
        if (value < 3)
            return;
// Create the first sifter, pass it links to the results label and the working label
// and the first number to sift and then start it running

// Reuse an existing results field if it exists
        OutputTextField resultsText;
        resultsText = resultsWindow.getFirstField();
        if (resultsText == null)
            resultsText = resultsWindow.addTextField(300, 100);

        OutputTextField workingLabel = workingWindow.addTextField("Starting...", 300, 100);

        Sifter firstSifter = new Sifter();
        firstSifter.setSiftValues(2);
        firstSifter.setMaxValue(value);
        firstSifter.setSiftLabel(workingLabel);
        firstSifter.setResultsText(resultsText);
        firstSifter.setWorkingWindow(workingWindow);

// Ввод чисел через класс feeder, работающий в отдельном потоке
        feeder = new Feeder(value);

// Устройство подачи создает очередь вывода по умолчанию, поэтому сначала нужно сообщить об этом просеивателю
        firstSifter.setReceiveQueue(feeder.getQueue());
        feeder.run();
        firstSifter.run();
        keeper.write(String.valueOf(resultsText));
    }


    public void resetFlag()
    {
        startButton.setText("Press me!");
        running = false;
    }
}

