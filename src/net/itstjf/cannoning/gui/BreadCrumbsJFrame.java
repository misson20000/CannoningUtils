package net.itstjf.cannoning.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.itstjf.cannoning.core.LiteModCannoning;
import net.itstjf.cannoning.core.Reference;
import net.itstjf.cannoning.core.Settings;
import net.itstjf.cannoning.epum.Explosions;
import net.itstjf.cannoning.epum.Rendering;
import net.itstjf.cannoning.mods.BreadcrumbsTNT;
import net.minecraft.util.StringUtils;

import java.awt.Color;

public class BreadCrumbsJFrame extends JFrame {
	LiteModCannoning litemod = LiteModCannoning.getInstance();
	Settings settings = litemod.settings;
	
	private JLabel lblCrumbs = new JLabel("Crumbs: 0");
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> listSelections = new JList<String>(listModel);
	
	private JButton btnRecordingButton;
	private JSlider sliderRecording;
	private JSpinner spinnerRecording;
	private JLabel lblUpdateUnknown;
	
	public BreadCrumbsJFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		this.setTitle("TNT Breadcrumbs | Its_its");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 760, 600);
		
		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths 	= new int[]{250, 250, 250, 0};
		gbl_contentPane.rowHeights 		= new int[]{243, 0};
		gbl_contentPane.columnWeights 	= new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights 		= new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		//-- Constraints
		
		GridBagConstraints gbc_panel_0 = new GridBagConstraints();
		gbc_panel_0.fill = GridBagConstraints.BOTH;
		gbc_panel_0.insets = new Insets(0, 0, 0, 5);
		gbc_panel_0.gridx = 0;
		gbc_panel_0.gridy = 0;
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 2);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 2;
		gbc_panel_2.gridy = 0;
		
		contentPane.add(this.panelBreadCrumbs(), gbc_panel_0);
		contentPane.add(this.panelCrumbsOptions(), gbc_panel_1);
		contentPane.add(this.panelRecording(), gbc_panel_2);
		
		this.setUpdate(Reference.newVersion);
		
		//--
		
		this.setContentPane(contentPane);
		
		this.updateSlider();
		
		for (BreadcrumbsTNT.UniqueEntity e : Reference.uniqueEntities) {
			this.addToSelection(e);
		}
	}
	
	private JPanel panelBreadCrumbs() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{243, 0};
		gbl_panel_1.rowHeights = new int[]{0, 28, 243, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		
		JLabel lblBreadcrumbs = new JLabel("Breadcrumbs");
		lblBreadcrumbs.setForeground(Color.WHITE);
		lblBreadcrumbs.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblBreadcrumbs = new GridBagConstraints();
		gbc_lblBreadcrumbs.insets = new Insets(0, 0, 5, 0);
		gbc_lblBreadcrumbs.gridx = 0;
		gbc_lblBreadcrumbs.gridy = 0;
		
		JButton btnRemoveSelections = new JButton("Remove Selections");
		
		GridBagConstraints gbc_btnRemoveSelections = new GridBagConstraints();
		gbc_btnRemoveSelections.anchor = GridBagConstraints.NORTH;
		gbc_btnRemoveSelections.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveSelections.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveSelections.gridx = 0;
		gbc_btnRemoveSelections.gridy = 1;
		this.listSelections.setForeground(Color.WHITE);
		this.listSelections.setBackground(Color.DARK_GRAY);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(listSelections);
		
		GridBagConstraints gbc_listSelections = new GridBagConstraints();
		gbc_listSelections.insets = new Insets(0, 0, 5, 2);
		gbc_listSelections.fill = GridBagConstraints.BOTH;
		gbc_listSelections.gridx = 0;
		gbc_listSelections.gridy = 2;
		
		final List list = new List(5, true);
		
		list.setBackground(Color.DARK_GRAY);
		list.add("TNT");
		list.add("Sand");
		list.add("Red Sand");
		list.add("Gravel");
		list.add("Anvil");
		list.select(0);
		list.select(1);
		list.select(2);
		list.select(3);
		list.select(4);
		
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.insets = new Insets(0, 0, 5, 0);
		gbc_list.gridx = 0;
		gbc_list.gridy = 3;
		
		GridBagConstraints gbc_lblCrumbs = new GridBagConstraints();
		gbc_lblCrumbs.gridx = 0;
		gbc_lblCrumbs.gridy = 4;
		
		panel.setLayout(gbl_panel_1);
		
		panel.add(lblBreadcrumbs, gbc_lblBreadcrumbs);
		panel.add(btnRemoveSelections, gbc_btnRemoveSelections);
		panel.add(scrollPane, gbc_listSelections);
		panel.add(list, gbc_list);
		this.lblCrumbs.setForeground(Color.WHITE);
		panel.add(lblCrumbs, gbc_lblCrumbs);
		
		btnRemoveSelections.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				BreadCrumbsJFrame _this = BreadCrumbsJFrame.this;
				Reference.linesViewing = null;
				_this.listSelections.getSelectionModel().clearSelection();
			}
		});
		
		this.listSelections.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == 3) {
					int index = ((JList)e.getSource()).locationToIndex(e.getPoint());
					if(index == -1) return;
					
					Reference.linesViewing = null;
					Reference.uniqueEntities.remove(index);
					listModel.remove(index);
					setCrumbAmount(listModel.size());
				}
			}
		});
		
		this.listSelections.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) return;
				int[] i = BreadCrumbsJFrame.this.listSelections.getSelectedIndices();
				Reference.linesViewing = i.length == 0 ? null : i;
			}
		});
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent a) {
				Reference.crumbsViewing = new boolean[] {
					list.isIndexSelected(0),
					list.isIndexSelected(1),
					list.isIndexSelected(2),
					list.isIndexSelected(3),
					list.isIndexSelected(4),
					true
				};
				
				BreadCrumbsJFrame.this.removeAllCrumbs();
				for (BreadcrumbsTNT.UniqueEntity e : Reference.uniqueEntities) {
					BreadCrumbsJFrame.this.addToSelection(e);
				}
			}
		});
		
		return panel;
	}
	
	private JScrollPane panelCrumbsOptions() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(panel);
		
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths 	= new int[] {61, 0};
		gbl_panel.rowHeights 	= new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights 	= new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		JLabel lblOptions = new JLabel("Breadcrumbs Options");
		lblOptions.setForeground(Color.WHITE);
		lblOptions.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOptions.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_lblOptions = new GridBagConstraints();
		gbc_lblOptions.insets = new Insets(0, 0, 5, 0);
		gbc_lblOptions.gridx = 0;
		gbc_lblOptions.gridy = 0;
		
		JLabel lblLineWidth = new JLabel("Line Width");
		lblLineWidth.setForeground(Color.WHITE);
		lblLineWidth.setBackground(Color.BLACK);
		GridBagConstraints gbc_lblLineWidth = new GridBagConstraints();
		gbc_lblLineWidth.anchor = GridBagConstraints.WEST;
		gbc_lblLineWidth.insets = new Insets(0, 2, 5, 0);
		gbc_lblLineWidth.gridx = 0;
		gbc_lblLineWidth.gridy = 1;
		
		JSpinner spinnerLineWidth = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		
		GridBagConstraints gbc_spinnerLineWidth = new GridBagConstraints();
		gbc_spinnerLineWidth.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerLineWidth.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerLineWidth.gridx = 0;
		gbc_spinnerLineWidth.gridy = 2;
		
		JCheckBox chckbxDisplay = new JCheckBox("Display");
		chckbxDisplay.setForeground(Color.LIGHT_GRAY);
		chckbxDisplay.setBackground(Color.DARK_GRAY);
		JCheckBox chckbxEnable = new JCheckBox("Enabled");
		chckbxEnable.setForeground(Color.LIGHT_GRAY);
		chckbxEnable.setBackground(Color.DARK_GRAY);
		
		JLabel lblDisappearTiming = new JLabel("Time Till Removal");
		lblDisappearTiming.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblDisappearTiming = new GridBagConstraints();
		gbc_lblDisappearTiming.anchor = GridBagConstraints.WEST;
		gbc_lblDisappearTiming.insets = new Insets(0, 0, 5, 0);
		gbc_lblDisappearTiming.gridx = 0;
		gbc_lblDisappearTiming.gridy = 3;
		
		JSlider sliderTTR = new JSlider();
		sliderTTR.setBackground(Color.DARK_GRAY);
		sliderTTR.setToolTipText("0 = No timing.");
		sliderTTR.setPaintLabels(true);
		sliderTTR.setPaintTicks(true);
		sliderTTR.setMaximum(60);
		sliderTTR.setMinorTickSpacing(1);
		sliderTTR.setMajorTickSpacing(10);
		sliderTTR.setLabelTable(sliderTTR.createStandardLabels(10));
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 5, 0);
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 4;
		
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.insets = new Insets(0, 0, 5, 0);
		gbc_2.anchor = GridBagConstraints.WEST;
		gbc_2.gridx = 0;
		gbc_2.gridy = 5;
		
		GridBagConstraints gbc_3 = new GridBagConstraints();
		gbc_3.insets = new Insets(0, 0, 5, 0);
		gbc_3.anchor = GridBagConstraints.WEST;
		gbc_3.gridx = 0;
		gbc_3.gridy = 6;
		
		JCheckBox chckbxDepth = new JCheckBox("Depth");
		chckbxDepth.setForeground(Color.LIGHT_GRAY);
		chckbxDepth.setBackground(Color.DARK_GRAY);
		
		GridBagConstraints gbc_00 = new GridBagConstraints();
		gbc_00.insets = new Insets(0, 0, 5, 0);
		gbc_00.anchor = GridBagConstraints.WEST;
		gbc_00.gridx = 0;
		gbc_00.gridy = 7;
		
		JCheckBox chckBxExplosion = new JCheckBox("Explosion Block");
		chckBxExplosion.setForeground(Color.LIGHT_GRAY);
		chckBxExplosion.setBackground(Color.DARK_GRAY);
		chckBxExplosion.setToolTipText("Display tnt explosion block");
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox.anchor = GridBagConstraints.WEST;
		gbc_checkBox.gridx = 0;
		gbc_checkBox.gridy = 8;
		
		JButton btnClearAllCrumbs = new JButton("Remove all Crumbs");
		GridBagConstraints gbc_btnClearAllCrumbs = new GridBagConstraints();
		gbc_btnClearAllCrumbs.insets = new Insets(0, 0, 5, 0);
		gbc_btnClearAllCrumbs.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnClearAllCrumbs.gridx = 0;
		gbc_btnClearAllCrumbs.gridy = 9;
		
		//--
		
		JLabel lblMinimalTNT = new JLabel("Minimal TNT");
		lblMinimalTNT.setForeground(Color.WHITE);
		lblMinimalTNT.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_4 = new GridBagConstraints();
		gbc_4.insets = new Insets(0, 0, 5, 0);
		gbc_4.anchor = GridBagConstraints.WEST;
		gbc_4.gridx = 0;
		gbc_4.gridy = 10;
		
		//--
		
		JLabel lblRendering = new JLabel("Rendering");
		lblRendering.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblRendering = new GridBagConstraints();
		gbc_lblRendering.insets = new Insets(0, 0, 5, 0);
		gbc_lblRendering.gridx = 0;
		gbc_lblRendering.gridy = 11;
		
		this.rdbtnEnableRendering = new JRadioButton("Enabled");
		rdbtnEnableRendering.setForeground(Color.LIGHT_GRAY);
		rdbtnEnableRendering.setBackground(Color.DARK_GRAY);
		this.rdbtnEnableRendering.addMouseListener(mtrListener);
		this.rdbtnEnableRendering.setToolTipText("Render All TNT");
		GridBagConstraints gbc_5 = new GridBagConstraints();
		gbc_5.insets = new Insets(0, 0, 5, 0);
		gbc_5.anchor = GridBagConstraints.WEST;
		gbc_5.gridx = 0;
		gbc_5.gridy = 12;
		
		this.rdbtnRemoveFromEntityRendering = new JRadioButton("Remove from Entity List");
		rdbtnRemoveFromEntityRendering.setForeground(Color.LIGHT_GRAY);
		rdbtnRemoveFromEntityRendering.setBackground(Color.DARK_GRAY);
		this.rdbtnRemoveFromEntityRendering.addMouseListener(mtrListener);
		this.rdbtnRemoveFromEntityRendering.setToolTipText("Removes entities in the same block from entity list (Removes from crumbs)");
		GridBagConstraints gbc_6 = new GridBagConstraints();
		gbc_6.insets = new Insets(0, 0, 5, 0);
		gbc_6.anchor = GridBagConstraints.WEST;
		gbc_6.gridx = 0;
		gbc_6.gridy = 13;
		
		this.rdbtnRemoveSameBlockRendering = new JRadioButton("Remove Rendering");
		rdbtnRemoveSameBlockRendering.setForeground(Color.LIGHT_GRAY);
		rdbtnRemoveSameBlockRendering.setBackground(Color.DARK_GRAY);
		this.rdbtnRemoveSameBlockRendering.addMouseListener(mtrListener);
		this.rdbtnRemoveSameBlockRendering.setToolTipText("Removes the rendering of tnt in the same block (Doesn't effect crumbs)");
		GridBagConstraints gbc_7 = new GridBagConstraints();
		gbc_7.insets = new Insets(0, 0, 5, 0);
		gbc_7.anchor = GridBagConstraints.WEST;
		gbc_7.gridx = 0;
		gbc_7.gridy = 14;
		
		this.rdbtnRemoveAllRendering = new JRadioButton("Remove All Rendering");
		rdbtnRemoveAllRendering.setForeground(Color.LIGHT_GRAY);
		rdbtnRemoveAllRendering.setBackground(Color.DARK_GRAY);
		this.rdbtnRemoveAllRendering.addMouseListener(mtrListener);
		this.rdbtnRemoveAllRendering.setToolTipText("Removes the rendering of all tnt (Doesn't effect crumbs)");
		GridBagConstraints gbc_8 = new GridBagConstraints();
		gbc_8.insets = new Insets(0, 0, 5, 0);
		gbc_8.anchor = GridBagConstraints.WEST;
		gbc_8.gridx = 0;
		gbc_8.gridy = 15;
		
		//---
		
		JLabel lblExplosions = new JLabel("Explosions");
		lblExplosions.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblExplosions = new GridBagConstraints();
		gbc_lblExplosions.insets = new Insets(0, 0, 5, 0);
		gbc_lblExplosions.gridx = 0;
		gbc_lblExplosions.gridy = 16;
		
		this.rdbtnEnableExplosions = new JRadioButton("Enabled");
		rdbtnEnableExplosions.setForeground(Color.LIGHT_GRAY);
		rdbtnEnableExplosions.setBackground(Color.DARK_GRAY);
		this.rdbtnEnableExplosions.addMouseListener(mteListener);
		this.rdbtnEnableExplosions.setToolTipText("Render All Explosions");
		this.rdbtnEnableExplosions.setSelected(true);
		GridBagConstraints gbc_9 = new GridBagConstraints();
		gbc_9.insets = new Insets(0, 0, 5, 0);
		gbc_9.anchor = GridBagConstraints.WEST;
		gbc_9.gridx = 0;
		gbc_9.gridy = 17;
		
		this.rdbtnRemoveSameBlockExplosions = new JRadioButton("Remove Explosions");
		rdbtnRemoveSameBlockExplosions.setForeground(Color.LIGHT_GRAY);
		rdbtnRemoveSameBlockExplosions.setBackground(Color.DARK_GRAY);
		this.rdbtnRemoveSameBlockExplosions.addMouseListener(mteListener);
		this.rdbtnRemoveSameBlockExplosions.setToolTipText("Remove explosions in the same block");
		GridBagConstraints gbc_10 = new GridBagConstraints();
		gbc_10.insets = new Insets(0, 0, 5, 0);
		gbc_10.anchor = GridBagConstraints.WEST;
		gbc_10.gridx = 0;
		gbc_10.gridy = 18;
		
		this.rdbtnRemoveNoVelocityExplosions = new JRadioButton("Remove No Velocity Explosions");
		rdbtnRemoveNoVelocityExplosions.setForeground(Color.LIGHT_GRAY);
		rdbtnRemoveNoVelocityExplosions.setBackground(Color.DARK_GRAY);
		this.rdbtnRemoveNoVelocityExplosions.addMouseListener(mteListener);
		this.rdbtnRemoveNoVelocityExplosions.setToolTipText("Remove explosions from tnt with no velocity");
		GridBagConstraints gbc_11 = new GridBagConstraints();
		gbc_11.insets = new Insets(0, 0, 5, 0);
		gbc_11.anchor = GridBagConstraints.WEST;
		gbc_11.gridx = 0;
		gbc_11.gridy = 19;
		
		this.rdbtnRemoveAllExplosions = new JRadioButton("Remove All Explosions");
		rdbtnRemoveAllExplosions.setForeground(Color.LIGHT_GRAY);
		rdbtnRemoveAllExplosions.setBackground(Color.DARK_GRAY);
		this.rdbtnRemoveAllExplosions.addMouseListener(mteListener);
		this.rdbtnRemoveAllExplosions.setToolTipText("Remove all explosions");
		GridBagConstraints gbc_rdbtnRemove = new GridBagConstraints();
		gbc_rdbtnRemove.anchor = GridBagConstraints.WEST;
		gbc_rdbtnRemove.gridx = 0;
		gbc_rdbtnRemove.gridy = 20;
		
		//-- Set defaults
		chckBxExplosion.setSelected(settings.blockExplosion);
		spinnerLineWidth.setValue(settings.lineWidth);
		chckbxDisplay.setSelected(settings.isRenderingCrumbs);
		chckbxEnable.setSelected(settings.enabled);
		sliderTTR.setValue(settings.removalTime);
		chckbxDepth.setSelected(settings.depth);
		
		if (settings.tntRendering == Rendering.ENABLED) {
			this.rdbtnEnableRendering.setEnabled(true);
		} else if (settings.tntRendering == Rendering.REMOVE_ENTITY_LIST) {
			this.rdbtnRemoveFromEntityRendering.setEnabled(true);
		} else if (settings.tntRendering == Rendering.SAME_BLOCK_RENDERING) {
			this.rdbtnRemoveSameBlockRendering.setEnabled(true);
		} else if (settings.tntRendering == Rendering.ALL_RENDERING) {
			this.rdbtnRemoveAllRendering.setEnabled(true);
		}
		
		if (settings.tntExplosions == Explosions.ENABLED) {
			this.rdbtnEnableExplosions.setEnabled(true);
		} else if (settings.tntExplosions == Explosions.ALL_EXPLOSIONS) {
			this.rdbtnRemoveAllExplosions.setEnabled(true);
		} else if (settings.tntExplosions == Explosions.NO_VELOCITY) {
			this.rdbtnRemoveNoVelocityExplosions.setEnabled(true);
		} else if (settings.tntExplosions == Explosions.SAME_BLOCK_EXPLOSIONS) {
			this.rdbtnRemoveSameBlockExplosions.setEnabled(true);
		}
		
		//-- Add to panel
		panel.setLayout(gbl_panel);
		panel.add(lblOptions, gbc_lblOptions);
		panel.add(lblLineWidth, gbc_lblLineWidth);
		panel.add(spinnerLineWidth, gbc_spinnerLineWidth);
		panel.add(lblDisappearTiming, gbc_lblDisappearTiming);
		panel.add(sliderTTR, gbc_slider);
		panel.add(chckbxEnable, gbc_2);
		panel.add(chckbxDisplay, gbc_3);
		panel.add(chckbxDepth, gbc_00);
		panel.add(chckBxExplosion, gbc_checkBox);
		panel.add(btnClearAllCrumbs, gbc_btnClearAllCrumbs);
		panel.add(lblMinimalTNT, gbc_4);
		panel.add(lblRendering, gbc_lblRendering);
		panel.add(this.rdbtnEnableRendering, gbc_5);
		panel.add(this.rdbtnRemoveFromEntityRendering, gbc_6);
		panel.add(this.rdbtnRemoveSameBlockRendering, gbc_7);
		panel.add(this.rdbtnRemoveAllRendering, gbc_8);
		panel.add(lblExplosions, gbc_lblExplosions);
		panel.add(this.rdbtnEnableExplosions, gbc_9);
		panel.add(this.rdbtnRemoveSameBlockExplosions, gbc_10);
		panel.add(this.rdbtnRemoveNoVelocityExplosions, gbc_11);
		panel.add(this.rdbtnRemoveAllExplosions, gbc_rdbtnRemove);
		
		ButtonGroup group1 = new ButtonGroup();
		ButtonGroup group2 = new ButtonGroup();
		
		group1.add(this.rdbtnEnableRendering);
		group1.add(this.rdbtnRemoveFromEntityRendering);
		group1.add(this.rdbtnRemoveSameBlockRendering);
		group1.add(this.rdbtnRemoveAllRendering);
		group2.add(this.rdbtnEnableExplosions);
		group2.add(this.rdbtnRemoveSameBlockExplosions);
		group2.add(this.rdbtnRemoveNoVelocityExplosions);
		group2.add(this.rdbtnRemoveAllExplosions);
		
		//-- Mouse Listeners
		
		sliderTTR.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider)e.getSource()).getValue();
				settings.removalTime = value;
			}
		});
		
		spinnerLineWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.lineWidth = Integer.parseInt(String.valueOf(((JSpinner)e.getSource()).getValue()));
			}
		});
		
		chckbxDisplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				settings.isRenderingCrumbs = !((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		chckBxExplosion.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				settings.blockExplosion = !((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		chckbxEnable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				settings.enabled = !((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		chckbxDepth.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				settings.depth = !((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		btnClearAllCrumbs.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				BreadcrumbsTNT.removeCrumbs = true;
				BreadCrumbsJFrame.this.listModel.clear();
				BreadCrumbsJFrame.this.setCrumbAmount(0);
			}
		});
		
		return scrollPane;
	}
	
	private JPanel panelRecording() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{39, 0};
		gbl_panel_2.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		JLabel lblReplay = new JLabel("Replay");
		lblReplay.setForeground(Color.WHITE);
		lblReplay.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 0;
		
		this.btnRecordingButton = new JButton("Start Recording");
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 1;
		
		this.sliderRecording = new JSlider();
		sliderRecording.setBackground(Color.DARK_GRAY);
		this.sliderRecording.setPaintLabels(true);
		this.sliderRecording.setPaintTicks(true);
		this.sliderRecording.setMinorTickSpacing(1);
		this.sliderRecording.setMajorTickSpacing(250);
		
		GridBagConstraints gbc_slider1 = new GridBagConstraints();
		gbc_slider1.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider1.insets = new Insets(0, 0, 5, 0);
		gbc_slider1.gridx = 0;
		gbc_slider1.gridy = 2;
		
		this.spinnerRecording = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
		
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 0;
		gbc_spinner.gridy = 3;
		
		JCheckBox chckbxDisplay_1 = new JCheckBox("Display");
		chckbxDisplay_1.setForeground(Color.LIGHT_GRAY);
		chckbxDisplay_1.setBackground(Color.DARK_GRAY);
		
		GridBagConstraints gbc_chckbxDisplay_1 = new GridBagConstraints();
		gbc_chckbxDisplay_1.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxDisplay_1.anchor = GridBagConstraints.WEST;
		gbc_chckbxDisplay_1.gridx = 0;
		gbc_chckbxDisplay_1.gridy = 4;
		
		JButton btnRemoveAllRecordings = new JButton("Remove all Recordings");
		
		GridBagConstraints gbc_btnRemoveAllRecordings = new GridBagConstraints();
		gbc_btnRemoveAllRecordings.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveAllRecordings.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveAllRecordings.gridx = 0;
		gbc_btnRemoveAllRecordings.gridy = 5;
		
		//-- Set defaults
		int maxValue = Reference.recordings.size() == 0 ? 0 : Reference.recordings.size() - 1;
		this.sliderRecording.setMaximum(maxValue);
		this.sliderRecording.setValue(0);
		((SpinnerNumberModel)this.spinnerRecording.getModel()).setMaximum(maxValue);
		chckbxDisplay_1.setSelected(settings.isRenderingRecording);
		
		//-- Add to Panel
		
		panel.setLayout(gbl_panel_2);
		panel.add(lblReplay, gbc_lblNewLabel_2);
		panel.add(this.btnRecordingButton, gbc_btnNewButton);
		panel.add(this.sliderRecording, gbc_slider1);
		panel.add(this.spinnerRecording, gbc_spinner);
		panel.add(chckbxDisplay_1, gbc_chckbxDisplay_1);
		panel.add(btnRemoveAllRecordings, gbc_btnRemoveAllRecordings);
		
		lblUpdateUnknown = new JLabel("Update: Unknown");
		lblUpdateUnknown.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblUpdateUnknow = new GridBagConstraints();
		gbc_lblUpdateUnknow.gridx = 0;
		gbc_lblUpdateUnknow.gridy = 18;
		panel.add(lblUpdateUnknown, gbc_lblUpdateUnknow);
		
		//-- Mouse Listeners
		
		this.btnRecordingButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Reference.isRecording = !Reference.isRecording;
				setButtonRecording(Reference.isRecording);
			}
		});
		
		this.sliderRecording.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider)e.getSource()).getValue();
				settings.displayRecording = value;
				BreadCrumbsJFrame.this.spinnerRecording.setValue(value);
			}
		});
		
		this.spinnerRecording.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = Integer.parseInt(String.valueOf(((JSpinner)e.getSource()).getValue()));
				settings.displayRecording = value;
				BreadCrumbsJFrame.this.sliderRecording.setValue(value);
			}
		});
		
		chckbxDisplay_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				settings.isRenderingRecording = !((JCheckBox)e.getSource()).isSelected();
			}
		});
		
		btnRemoveAllRecordings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				setButtonRecording(false);
				Reference.isRecording = false;
				Reference.recordings.clear();
				updateSlider();
			}
		});
		
		return panel;
	}
	
	JRadioButton rdbtnEnableRendering, rdbtnRemoveFromEntityRendering, rdbtnRemoveSameBlockRendering, rdbtnRemoveAllRendering,
			 rdbtnEnableExplosions, rdbtnRemoveSameBlockExplosions, rdbtnRemoveNoVelocityExplosions, rdbtnRemoveAllExplosions;
	
	public void addToSelection(BreadcrumbsTNT.UniqueEntity e) {
		if(!e.canView()) return;
		this.listModel.addElement(StringUtils.stripControlCodes(e.name));
		this.setCrumbAmount(this.listModel.size());
	}
	
	public void removeAllCrumbs() {
		this.listModel.clear();
		this.setCrumbAmount(0);
	}
	
	public void setCrumbAmount(int amount) {
		this.lblCrumbs.setText("Crumbs: " + amount);
	}
	
	public void setUpdate(boolean update) {
		this.lblUpdateUnknown.setText("Update: " + (update ? "New Version available" : "Latest Version"));
	}
	
	class MTRListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			JRadioButton button = (JRadioButton)e.getSource();
			if(button == BreadCrumbsJFrame.this.rdbtnEnableRendering) {
				settings.tntRendering = Rendering.ENABLED;
			} else if(button == BreadCrumbsJFrame.this.rdbtnRemoveFromEntityRendering) {
				settings.tntRendering = Rendering.REMOVE_ENTITY_LIST;
			} else if(button == BreadCrumbsJFrame.this.rdbtnRemoveSameBlockRendering) {
				settings.tntRendering = Rendering.SAME_BLOCK_RENDERING;
			} else if(button == BreadCrumbsJFrame.this.rdbtnRemoveAllRendering) {
				settings.tntRendering = Rendering.ALL_RENDERING;
			}
		}
	}
	
	class MTEListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			JRadioButton button = (JRadioButton)e.getSource();
			if(button == BreadCrumbsJFrame.this.rdbtnEnableExplosions) {
				settings.tntExplosions = Explosions.ENABLED;
			} else if(button == BreadCrumbsJFrame.this.rdbtnRemoveSameBlockExplosions) {
				settings.tntExplosions = Explosions.SAME_BLOCK_EXPLOSIONS;
			} else if(button == BreadCrumbsJFrame.this.rdbtnRemoveNoVelocityExplosions) {
				settings.tntExplosions = Explosions.NO_VELOCITY;
			} else if(button == BreadCrumbsJFrame.this.rdbtnRemoveAllExplosions) {
				settings.tntExplosions = Explosions.ALL_EXPLOSIONS;
			}
		}
	}
	
	public MTRListener mtrListener = new MTRListener();
	public MTEListener mteListener = new MTEListener();
	
	public JButton getBtnRecording() {
		return this.btnRecordingButton;
	}
	
	public void updateSlider() {
		settings.displayRecording = 0;
		this.sliderRecording.setValue(0);
		this.spinnerRecording.setValue(0);
		int size = Reference.recordings.size();
		this.sliderRecording.setMaximum(size == 0 ? 0 : size - 1);
		((SpinnerNumberModel)this.spinnerRecording.getModel()).setMaximum(size == 0 ? 0 : size - 1);
	}
	
	public void setButtonRecording(boolean isRecording) {
		this.btnRecordingButton.setText((isRecording ? "Stop" : "Start") + " Recording");
		if(isRecording) litemod.moduleRecording.lastEntity = System.currentTimeMillis() + 12000L;
		else updateSlider();
	}
}