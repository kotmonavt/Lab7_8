import java.util.LinkedList;

// Класс пул для взаимодействия с LinkedList в многопоточном режиме
public class URLPool {
    // список для хранения доступных ссылок
    private final LinkedList<URLBuilder> openedPairs = new LinkedList<>();
    // список для хранения пройденных ссылок
    private final LinkedList<URLBuilder> closedPairs = new LinkedList<>();
    //Переменная, учитывающая сколько потоков ожидает ссылок в пуле
    private int numWaiters = 0;

    // Функция для получения ссылки из списка необработанных
    public synchronized URLBuilder pop(){
        while(isEmpty()){
            numWaiters++;
            try {
                wait();
            }
            catch (InterruptedException e) {}
            numWaiters--;
        }
        return openedPairs.removeFirst();
    }

    public int getWaiters(){
        return numWaiters;
    }

    // Функция для добавления ссылки в список необработанных
    public synchronized boolean push(URLBuilder pair){
        if(!openedPairs.contains(pair) && !closedPairs.contains(pair)){
            openedPairs.add(pair);
            notify();
            return true;
        }
        return false;
    }

    // Функция, проверяющая пуст ли список необработанных ссылок
    public boolean isEmpty(){
        return openedPairs.isEmpty();
    }

    // Функция для добавления ссылки в список обработанных
    public synchronized void migrate(URLBuilder pair){
        if(!closedPairs.contains(pair)){
            closedPairs.add(pair);
        }
    }

    public LinkedList<URLBuilder> getOpenedPairs(){
        return openedPairs;
    }

    public LinkedList<URLBuilder> getClosedPairs(){
        return closedPairs;
    }
}