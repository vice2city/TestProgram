import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 实现设置窗口的行为
 * @author 朱洪椿
 */
public class SettingWindow
{
    private final JDialog baseDialog;

    public SettingWindow(JFrame window)
    {
        // 创建 Dialog
        baseDialog = new JDialog(window, "设置", true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        baseDialog.setBounds(dimension.width/2-150, dimension.height/2-100, 300, 200);
        JPanel basePanel = new JPanel(new GridLayout(3, 2, 10,10));
        basePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        baseDialog.setContentPane(basePanel);
        // 考试时间设置
        JLabel timeLabel = new JLabel("考试限时（秒）：", JLabel.RIGHT);
        JTextField timeField = new JTextField(String.valueOf(TestSystem.instance.timeSeconds));
        // 试题数量设置
        JLabel numLabel = new JLabel("试题数量：", JLabel.RIGHT);
        JTextField numField = new JTextField(String.valueOf(TestSystem.instance.testNumber));
        // 按钮：打开试题配置文件
        JButton openQuestionFile = new JButton("配置试题");
        openQuestionFile.setBackground(new Color(233, 196, 106));
        openQuestionFile.addActionListener(event -> {
            try
            {
                File file = new File(TestSystem.filePath);
                Desktop.getDesktop().open(file);
            }
            catch (IOException | NullPointerException e)
            {
                throw new RuntimeException(e);
            }
        });
        // 按钮：确定设置选项并过滤非数字输入
        JButton confirm = new JButton("确定");
        confirm.setBackground(new Color(42, 157, 143));
        confirm.setForeground(Color.white);
        confirm.addActionListener(e -> {
            int time = Integer.parseInt(timeField.getText().replaceAll("\\D+", ""));
            int num = Integer.parseInt(numField.getText().replaceAll("\\D+", ""));
            TestSystem.instance.timeSeconds = time > 0 ? time : TestSystem.instance.timeSeconds;
            TestSystem.instance.testNumber = num > 0 ? num : TestSystem.instance.testNumber;
            baseDialog.dispose();
        });

        basePanel.add(timeLabel);
        basePanel.add(timeField);
        basePanel.add(numLabel);
        basePanel.add(numField);
        basePanel.add(openQuestionFile);
        basePanel.add(confirm);
        // 显示对话框
        baseDialog.setVisible(true);
        baseDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}