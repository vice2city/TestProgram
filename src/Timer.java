import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 计时器
 * @author 朱洪椿
 */
public class Timer
{
    private ScheduledExecutorService service;
    private final Runnable runnable;
    private final List<ActionListener> listenerList;
    private int nowSecond;

    Timer()
    {
        listenerList = new ArrayList<>();
        runnable = () -> {
            nowSecond++;
            for (ActionListener listener : listenerList)
            {
                listener.actionPerformed(new ActionEvent(this, nowSecond, "timer!"));
            }
        };
    }

    public void Start()
    {
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
    }

    public void Stop()
    {
        service.shutdown();
    }

    public void AddListener(ActionListener listener)
    {
        listenerList.add(listener);
    }

    public int GetSeconds()
    {
        return nowSecond;
    }
}
