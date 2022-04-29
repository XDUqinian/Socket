package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends JFrame
{
    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    // ���崰��Ŀ��
    public int windowsWidth = 600;
    public int windowsHeight = 600;
    
    @SuppressWarnings("deprecation")
	public void resize(int windowsWidth, int windowsHeight)
    {
        // ���ô���λ�úʹ�С
        super.resize(windowsWidth, windowsHeight);
        this.windowsHeight = windowsHeight;
        this.windowsHeight = windowsWidth;
        this.setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);
    }

    public Window(String title)
    {
        this.setTitle(title);
        // �رմ���
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent windowEvent)
            {
                System.exit(0);
            }
        });
    }

    public Window()
    {
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent windowEvent)
            {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args)
    {
        new Window().setVisible(true);
    }
}
