package websocket.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;
import websocket.util.ByteUtil;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PacketViewer extends ViewPart {
	public static final String ID = "z1.views.SampleView";
	private Action action1;
	private Action action2;
	private Text packet;
	private Text fin1;
	private Text fin2;
	private Text data1;
	private Text rsv1;
	private Text rsv2;
	private Text op1;
	private Text op2;
	private Text mask1;
	private Text mask2;
	private Text len1;
	private Text len2;
	private Text exlenA1;
	private Text exlenA2;
	private Text exlenB1;
	private Text exlenB2;
	private Text key1;
	private Text key2;
	private Text data2;
	private Text fin3;
	private Text rsv3;
	private Text op3;
	private Text mask3;
	private Text len3;
	private Text exlenA3;
	private Text exlenB3;
	private Text key3;

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	public PacketViewer() {
	}

	public void formClear(){
		packet.setText("");
		fin1.setText("");
		fin2.setText("");
		fin3.setText("");
		rsv1.setText("");
		rsv2.setText("");
		rsv3.setText("");
		op1.setText("");
		op2.setText("");
		op3.setText("");
		mask1.setText("");
		mask2.setText("");
		mask3.setText("");
		len1.setText("");
		len2.setText("");
		len3.setText("");
		exlenA1.setText("");
		exlenA2.setText("");
		exlenA3.setText("");
		exlenB1.setText("");
		exlenB2.setText("");
		exlenB3.setText("");
		key1.setText("");
		key2.setText("");
		key3.setText("");
		data1.setText("");
		data2.setText("");
	}
	public void packetAnalyze(){
//		String hex = "81a405c1245334f3176730f7136b3cf1453166a5413562a94d396ead493d6ab1552176b5512572b95d29";
//		packet.setText(hex);

		byte[] key = new byte[4];
		int keyPos = 0;
		int dataPos = 0;
		int dataLength = 0;

		byte[] bytePacket = ByteUtil.hexToByteArray(packet.getText());
		
		int fin = ByteUtil.getBitRange(bytePacket[0],7,1);
		fin1.setText(String.format("%02x%n",fin));
		fin2.setText(fin + ".......");
		if(fin==1) fin3.setText("FIN = TRUE (Last packet)");
		else fin3.setText("FIN = FALSE (Exists next packet)");
		
		int rsv = ByteUtil.getBitRange(bytePacket[0],6,3);
		rsv1.setText(String.format("%02x%n",rsv));
		rsv2.setText("." + ByteUtil.getBitRangeString(bytePacket[0],6,3) + "....");
		rsv3.setText("Reserved: 0x" + rsv);
		
		int op = ByteUtil.getBitRange(bytePacket[0],3,4);
		op1.setText(String.format("%02x%n",op));
		op2.setText("...." + ByteUtil.getBitRangeString(bytePacket[0],3,4));
		op3.setText("Opcode: " + op);
		
		int mask = ByteUtil.getBitRange(bytePacket[1],7,1);
		mask1.setText(String.format("%02x%n",mask));
		mask2.setText(fin + ".......");
		if(mask==1) mask3.setText("Mask = TRUE");
		else mask3.setText("Mask = FALSE");
		
		int len = ByteUtil.getBitRange(bytePacket[1],6,7);
		len1.setText(String.format("%02x%n",len));
		len2.setText("." + ByteUtil.getBitRangeString(bytePacket[0],6,7));
		len3.setText("Payload Length = " + len + "   (126=Ex1, 127=Ex2)");

		keyPos=2;
		dataPos=2;
		if(len<126){
			dataLength = len;
			exlenA1.setText(""); exlenA2.setText(""); exlenA3.setText("´ë»ó ¾Æ´Ô");
			exlenB1.setText(""); exlenB2.setText(""); exlenB3.setText("´ë»ó ¾Æ´Ô");
		}else if(len==126){
			exlenB1.setText(""); exlenB2.setText(""); exlenB3.setText("´ë»ó ¾Æ´Ô");
			byte[] tmpb = Arrays.copyOfRange(bytePacket,2,4);
			int exlenA = ByteUtil.bytesToShort(tmpb);
			exlenA1.setText(String.format("%02x%n", exlenA));
			exlenA2.setText(ByteUtil.getBitString(tmpb[0]) + ByteUtil.getBitString(tmpb[1]));
			exlenA3.setText("Length = " + exlenA);
			keyPos += 2; // Ex LenA = 2byte 
			dataPos += 2;
			dataLength = exlenA;
		}else if(len==127){
			exlenA1.setText(""); exlenA2.setText(""); exlenA3.setText("´ë»ó ¾Æ´Ô");
			byte[] tmpb = Arrays.copyOfRange(bytePacket, 2, 10);
			int exlenB = ByteUtil.bytesToShort(tmpb);
			exlenB1.setText(String.format("%02x%n", exlenB));
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<8;i++) { 
				if(i>0) sb.append(" ");
				sb.append(ByteUtil.getBitString(tmpb[i])); 
			}
			exlenB2.setText(sb.toString());
			exlenB3.setText("Length = " + exlenB);
			keyPos += 8; // Ex LenB = 8byte
			dataPos += 8; 
			dataLength = exlenB;
		}
		
		if(mask==1){
			key = Arrays.copyOfRange(bytePacket, keyPos, keyPos+4);
			String hexKeyStr = ByteUtil.byteArrayToHex(key);
			key3.setText(hexKeyStr);
			dataPos += 4; // Mask Key = 4byte
		} else {
			key1.setText("");
			key2.setText("");
			key3.setText("´ë»ó¾Æ´Ô");
		}

		byte[] tmpb = Arrays.copyOfRange(bytePacket, dataPos, dataPos+dataLength);
		if(mask==1){
			for(int i=0;i<tmpb.length;i++){
				tmpb[i] = (byte)(tmpb[i] ^ key[i%4]);
			}
		}
		data1.setText(  new String(tmpb)  );
	}

	public void createPartControl(Composite parent) {
		parent.setFont(SWTResourceManager.getFont("¸¼Àº °íµñ", 10, SWT.NORMAL));
		parent.setLayout(null);
		
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblNewLabel.setBounds(23, 25, 42, 15);
		lblNewLabel.setText("Packet");
		
		packet = new Text(parent, SWT.BORDER);
		packet.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		packet.setBounds(87, 24, 481, 21);
		
		Button btnAnalyze = new Button(parent, SWT.NONE);
		btnAnalyze.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnAnalyze.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		btnAnalyze.setBounds(21, 51, 57, 25);
		btnAnalyze.setText("Analyze");
		
		Listener btnAnalyzeListener = new Listener() {
			public void handleEvent(Event event) {
				packetAnalyze();
			}
		};
		btnAnalyze.addListener(SWT.Selection, btnAnalyzeListener);		
		
		fin1 = new Text(parent, SWT.BORDER);
		fin1.setBackground(SWTResourceManager.getColor(255, 255, 224));
		fin1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		fin1.setBounds(87, 80, 22, 21);
		
		fin2 = new Text(parent, SWT.BORDER);
		fin2.setBackground(SWTResourceManager.getColor(255, 255, 224));
		fin2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		fin2.setBounds(113, 80, 72, 21);
		
		Label lblFin = new Label(parent, SWT.NONE);
		lblFin.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblFin.setAlignment(SWT.RIGHT);
		lblFin.setBounds(21, 82, 55, 15);
		lblFin.setText("FIN");
		
		Label lblNewLabel_1 = new Label(parent, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBounds(21, 108, 55, 15);
		lblNewLabel_1.setText("Reserved");
		
		Label lblOpcode = new Label(parent, SWT.NONE);
		lblOpcode.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblOpcode.setAlignment(SWT.RIGHT);
		lblOpcode.setBounds(21, 134, 55, 15);
		lblOpcode.setText("Opcode");
		
		Label lblMask = new Label(parent, SWT.NONE);
		lblMask.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblMask.setAlignment(SWT.RIGHT);
		lblMask.setBounds(21, 163, 55, 15);
		lblMask.setText("Mask");
		
		Label lblLength = new Label(parent, SWT.NONE);
		lblLength.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblLength.setAlignment(SWT.RIGHT);
		lblLength.setBounds(21, 190, 55, 15);
		lblLength.setText("Length");
		
		Label lblstExLength = new Label(parent, SWT.NONE);
		lblstExLength.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblstExLength.setAlignment(SWT.RIGHT);
		lblstExLength.setBounds(21, 217, 55, 15);
		lblstExLength.setText("Ex Len1");
		
		Label lblndExLength = new Label(parent, SWT.NONE);
		lblndExLength.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblndExLength.setAlignment(SWT.RIGHT);
		lblndExLength.setBounds(21, 244, 55, 15);
		lblndExLength.setText("Ex Len2");
		
		Label lblMaskKey = new Label(parent, SWT.NONE);
		lblMaskKey.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblMaskKey.setAlignment(SWT.RIGHT);
		lblMaskKey.setBounds(21, 270, 55, 15);
		lblMaskKey.setText("MaskKey");
		
		Label lblData = new Label(parent, SWT.NONE);
		lblData.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		lblData.setBounds(21, 301, 56, 15);
		lblData.setText("Data");
		
		data1 = new Text(parent, SWT.BORDER);
		data1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		data1.setBounds(23, 322, 545, 74);
		
		rsv1 = new Text(parent, SWT.BORDER);
		rsv1.setBackground(SWTResourceManager.getColor(255, 255, 224));
		rsv1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		rsv1.setBounds(87, 107, 22, 21);
		
		rsv2 = new Text(parent, SWT.BORDER);
		rsv2.setBackground(SWTResourceManager.getColor(255, 255, 224));
		rsv2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		rsv2.setBounds(113, 107, 72, 21);
		
		op1 = new Text(parent, SWT.BORDER);
		op1.setBackground(SWTResourceManager.getColor(255, 255, 224));
		op1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		op1.setBounds(87, 134, 22, 21);
		
		op2 = new Text(parent, SWT.BORDER);
		op2.setBackground(SWTResourceManager.getColor(255, 255, 224));
		op2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		op2.setBounds(113, 134, 72, 21);
		
		mask1 = new Text(parent, SWT.BORDER);
		mask1.setBackground(SWTResourceManager.getColor(245, 245, 220));
		mask1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		mask1.setBounds(87, 161, 22, 21);
		
		mask2 = new Text(parent, SWT.BORDER);
		mask2.setBackground(SWTResourceManager.getColor(245, 245, 220));
		mask2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		mask2.setBounds(113, 161, 72, 21);
		
		len1 = new Text(parent, SWT.BORDER);
		len1.setBackground(SWTResourceManager.getColor(245, 245, 220));
		len1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		len1.setBounds(87, 188, 22, 21);
		
		len2 = new Text(parent, SWT.BORDER);
		len2.setBackground(SWTResourceManager.getColor(245, 245, 220));
		len2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		len2.setBounds(113, 188, 72, 21);
		
		exlenA1 = new Text(parent, SWT.BORDER);
		exlenA1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		exlenA1.setBounds(87, 215, 22, 21);
		
		exlenA2 = new Text(parent, SWT.BORDER);
		exlenA2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		exlenA2.setBounds(113, 215, 72, 21);
		
		exlenB1 = new Text(parent, SWT.BORDER);
		exlenB1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		exlenB1.setBounds(87, 242, 85, 21);
		
		exlenB2 = new Text(parent, SWT.BORDER);
		exlenB2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		exlenB2.setBounds(178, 242, 83, 21);
		
		key1 = new Text(parent, SWT.BORDER);
		key1.setBackground(SWTResourceManager.getColor(255, 192, 203));
		key1.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		key1.setBounds(87, 268, 55, 21);
		
		key2 = new Text(parent, SWT.BORDER);
		key2.setBackground(SWTResourceManager.getColor(255, 192, 203));
		key2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		key2.setBounds(148, 268, 113, 21);
		
		data2 = new Text(parent, SWT.BORDER);
		data2.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		data2.setBounds(23, 402, 545, 74);
		
		fin3 = new Text(parent, SWT.BORDER);
		fin3.setBackground(SWTResourceManager.getColor(255, 255, 224));
		fin3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		fin3.setBounds(190, 80, 377, 21);
		
		rsv3 = new Text(parent, SWT.BORDER);
		rsv3.setBackground(SWTResourceManager.getColor(255, 255, 224));
		rsv3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		rsv3.setBounds(190, 107, 377, 21);
		
		op3 = new Text(parent, SWT.BORDER);
		op3.setBackground(SWTResourceManager.getColor(255, 255, 224));
		op3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		op3.setBounds(190, 134, 377, 21);
		
		mask3 = new Text(parent, SWT.BORDER);
		mask3.setBackground(SWTResourceManager.getColor(245, 245, 220));
		mask3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		mask3.setBounds(190, 161, 377, 21);
		
		len3 = new Text(parent, SWT.BORDER);
		len3.setBackground(SWTResourceManager.getColor(245, 245, 220));
		len3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		len3.setBounds(190, 188, 377, 21);
		
		exlenA3 = new Text(parent, SWT.BORDER);
		exlenA3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		exlenA3.setBounds(190, 215, 377, 21);
		
		exlenB3 = new Text(parent, SWT.BORDER);
		exlenB3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		exlenB3.setBounds(266, 242, 301, 21);
		
		key3 = new Text(parent, SWT.BORDER);
		key3.setBackground(SWTResourceManager.getColor(255, 192, 203));
		key3.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		key3.setBounds(266, 268, 301, 21);
		
		Button btnClear = new Button(parent, SWT.NONE);
		btnClear.setBounds(80, 51, 57, 25);
		btnClear.setText("Clear");
		
		Listener btnClearListener = new Listener() {
			public void handleEvent(Event event) {
				formClear();
			}
		};
		btnClear.addListener(SWT.Selection, btnClearListener);		
		
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PacketViewer.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//		doubleClickAction = new Action() {
//			public void run() {
//			}
//		};
	}

	private void hookDoubleClickAction() {
	}
	private void showMessage(String message) {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
//		viewer.getControl().setFocus();
	}
}
