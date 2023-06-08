import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * 测试窗口
 * @author 朱洪椿
 */
public class TestWindow
{
    private final JFrame mainWindow;
    private final Container testContainer;
    private final CardLayout layout;
    private final JFrame testWindow;
    private QuestionPanel questionPanel;

    public TestWindow(JFrame window)
    {
        mainWindow = window;
        testWindow = new JFrame("答题界面");
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        testWindow.setBounds(dimension.width/2-450, dimension.height/2-300, 900, 600);
        testWindow.setResizable(false);
        testWindow.setIconImage(new ImageIcon(ImagePath.iconImage).getImage());
        // 应用卡片布局类型
        testContainer = testWindow.getContentPane();
        layout = new CardLayout();
        testContainer.setLayout(layout);
        // 使用多线程打开加载页面
        LoadingPanel loadingPanel = new LoadingPanel(this);
        testContainer.add(loadingPanel.basePanel, "loading");
        new Thread(loadingPanel).start();
        testWindow.setVisible(true);
        testWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (questionPanel!=null) questionPanel.Stop();
                super.windowClosed(e);
                mainWindow.setVisible(true);
            }
        });
        testWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * 开始测试
     * @param panel 测试页面的引用
     */
    public void StartTest(QuestionPanel panel)
    {
        questionPanel = panel;
        testContainer.add(panel.basePanel, "question");
        layout.show(testContainer, "question");
        questionPanel.Start();
    }

    /**
     * 提交测试
     * @param questions 题目列表
     * @param answers 回答列表
     * @param seconds 完成用时
     */
    public void SubmitTest(List<Question> questions, List<String> answers, int seconds)
    {
        questionPanel.Stop();
        SubmitPanel submitPanel = new SubmitPanel(testWindow, questions, answers, seconds);
        testContainer.add(submitPanel.basePanel, "submit");
        layout.show(testContainer, "submit");
    }

}

/**
 * 加载页面
 * @author 朱洪椿
 */
class LoadingPanel implements Runnable
{
    public final JPanel basePanel;
    private final TestWindow testWindow;
    public LoadingPanel(TestWindow window)
    {
        testWindow = window;
        basePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 400));
        basePanel.setBackground(new Color(42, 157, 143));
        JLabel text = new JLabel("加载中...");
        text.setForeground(new Color(255, 255, 255));
        text.setFont(new Font("黑体", Font.PLAIN ,20));
        basePanel.add(text, BorderLayout.CENTER);
    }

    @Override
    public void run() {
        synchronized (this){
            List<Question> list = TestSystem.instance.GetExamination(10);
            QuestionPanel panel = new QuestionPanel(testWindow, list);
            try {
                wait(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            testWindow.StartTest(panel);
        }
    }
}

/**
 * 提交结果页面
 * @author 朱洪椿
 */
class SubmitPanel
{
    public final JPanel basePanel;

    public SubmitPanel(JFrame mainWindow, List<Question> questions, List<String> answers, int seconds)
    {
        int score = TestSystem.GetScore(questions, answers);

        basePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        basePanel.setBorder(new EmptyBorder(20, 20, 20,20));
        basePanel.setBackground(new Color(231, 111, 81));
        // 文本框
        JLabel label1 = new JLabel("你的得分是：", SwingConstants.CENTER);
        label1.setFont(new Font("黑体", Font.PLAIN, 18));
        label1.setForeground(new Color(233, 196, 106));
        label1.setPreferredSize(new Dimension(700, 50));
        // 分数
        JLabel scoreLabel = new JLabel(String.valueOf(score), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("黑体", Font.BOLD, 120));
        scoreLabel.setPreferredSize(new Dimension(750, 120));
        scoreLabel.setForeground(Color.white);
        // 按钮部分
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(791, 110));
        // 按钮：导出试卷
        JButton exportButton = new JButton("导出");
        exportButton.setPreferredSize(new Dimension(100, 40));
        exportButton.setFont(new Font("黑体", Font.PLAIN, 15));
        exportButton.setBackground(new Color(42, 157, 143));
        exportButton.setForeground(Color.white);
        exportButton.addActionListener(e -> {
            TestSystem.instance.ExportTest(questions, answers, seconds);
            JOptionPane.showMessageDialog(basePanel, "导出完成!", "导出", JOptionPane.INFORMATION_MESSAGE);
        });
        // 分割线
        JPanel line = new JPanel();
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(790, 1));
        // 按钮：返回主界面
        JButton backButton = new JButton("返回");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setFont(new Font("黑体", Font.PLAIN, 15));
        backButton.setBackground(new Color(42, 157, 143));
        backButton.setForeground(Color.white);
        backButton.addActionListener(e -> mainWindow.dispose());
        // 显示页面
        basePanel.add(label1);
        basePanel.add(scoreLabel);
        buttonPanel.add(exportButton);
        buttonPanel.add(line);
        buttonPanel.add(backButton);
        basePanel.add(buttonPanel);
    }
}
