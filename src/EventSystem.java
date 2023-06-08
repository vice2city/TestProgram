import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * 事件系统：处理主窗口中三个按钮按下的事件
 * @author 朱洪椿
 */
public class EventSystem implements ActionListener
{
    private JFrame mainWindow;
    public JButton startTest;
    public JButton openHistory;
    public JButton openSettings;

    public EventSystem()
    {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == startTest)
        {
            StartTest();
        }
        else if (e.getSource() == openHistory)
        {
            OpenHistory();
        }
        else if (e.getSource() == openSettings)
        {
            OpenSettings();
        }
    }

    /**
     * 注册按钮
     * @param window 主窗口
     */
    public void RegisterButton(JFrame window)
    {
        mainWindow = window;
        startTest.addActionListener(this);
        openHistory.addActionListener(this);
        openSettings.addActionListener(this);
    }

    private void StartTest()
    {
        mainWindow.dispose();
        new TestWindow(mainWindow);
    }

    private void OpenHistory()
    {
        File f1=new File("resources/tests");
        try {
            Desktop.getDesktop().open(f1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void OpenSettings()
    {
        new SettingWindow(mainWindow);
    }
}
