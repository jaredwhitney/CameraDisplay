import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.usfirst.frc.team4761.robot.DataPacket;
public class DataStreamManager extends JComponent implements Runnable
{
	Socket s;
	ObjectInputStream in;
	BufferedImage i = new BufferedImage(3, 3, 3);
	double rots = 45;
	JFrame window;
	public DataStreamManager() throws Exception
	{
		System.out.println("Waiting on roborio...");
		s = new Socket("roboRIO-4761.local", 1707);
		in = new ObjectInputStream(s.getInputStream());
		System.out.println("Connected to the robot!");
		window = new JFrame();
		window.add(this);
		window.setSize(50, 50);
		window.setVisible(true);
		new Thread(this).start();
	}
	public void test() throws Exception
	{
		while (true)
		{
			DataPacket packet = (DataPacket)in.readObject();
			System.out.println(packet.key + ": " + packet.data);
			if (packet.key.equals("Camera Image"))
			{
				byte[] dbyte = (byte[])packet.data;
				int q = 0;
				for (int n=0; n<dbyte.length; n+=3)
				{
					System.out.println("Color: " + (((int)dbyte[n])&255) + ", " + (((int)dbyte[n+1])&255) + ", " + (((int)dbyte[n+2])&255));
					i.setRGB(q%i.getWidth(),q/i.getWidth(),new Color((((int)dbyte[n])&255), (((int)dbyte[n+1])&255), (((int)dbyte[n+2])&255)).getRGB());
					q++;
				}
				File f = new File("C:/Users/Robockets/Desktop/IT_WORKED.png");
				f.createNewFile();
			//	ImageIO.write(i, "png", f);
			//	System.out.println("Image written.");
			}
			else
				rots = (double)packet.data;
		}
	}
	public void paint(Graphics g)
	{
		AffineTransform rotate = new AffineTransform();
		rotate.rotate(rots);
		rotate.scale(50, 50);
		((Graphics2D)g).drawImage(i, rotate, null);
	}
	public void run()
	{
		while (true)
		{
			window.invalidate();
			window.repaint();
		}
	}
}
