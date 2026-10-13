package com.security.ui;

import com.security.database.DatabaseManager;
import com.security.detector.ThreatDetector;
import com.security.model.AuthSecurityEvent;
import com.security.model.SecurityEvent;
import com.security.model.SensorSecurityEvent;
import com.security.model.ThreatLevel;
import com.security.service.LogSimulator;
import com.security.service.SensorMonitorThread;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Smart Home Security Threat Monitor
 * 100% Pure Java Desktop Application (Java Swing)
 * Designed to mirror the modern web experience purely using Java.
 * Features:
 * - Home (Hero & Introduction)
 * - Live Security Dashboard (Sensor matrix & status indicators)
 * - Threat Alerts & Incident History (JTable, filters & explainable heuristic AI)
 * - How It Works (Pipeline & Architecture)
 * - Project Details & Database Schema (SQLite specifications & hardware placeholders)
 * - Team & Contact (Placeholders for group members & submission)
 */
public class SmartHomeSecurityApp extends JFrame {

    // Dark Navy Security Palette (Matching the web design theme)
    public static final Color BG_DEEP = new Color(9, 14, 26);
    public static final Color BG_MAIN = new Color(15, 23, 42);
    public static final Color BG_CARD = new Color(22, 34, 56);
    public static final Color BG_SURFACE = new Color(17, 27, 46);
    public static final Color BORDER_COLOR = new Color(36, 52, 77);

    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);
    public static final Color TEXT_DIM = new Color(100, 116, 139);

    public static final Color COLOR_SAFE = new Color(16, 185, 129);
    public static final Color COLOR_WARNING = new Color(245, 158, 11);
    public static final Color COLOR_DANGER = new Color(239, 68, 68);
    public static final Color COLOR_CYAN = new Color(56, 189, 248);
    public static final Color COLOR_BLUE = new Color(2, 132, 199);

    // Business Logic Instances
    private final ThreatDetector detector = new ThreatDetector();
    private final LogSimulator simulator = new LogSimulator();
    private final List<SecurityEvent> eventsList = new ArrayList<>();

    // Background Thread
    private SensorMonitorThread monitorWorker;
    private Thread monitorThread;

    // Navigation & Views
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final List<JButton> navButtons = new ArrayList<>();

    // Dashboard KPI Labels
    private JLabel statusBadgeLabel;
    private JLabel totalEventsLabel;
    private JLabel criticalEventsLabel;
    private JLabel elevatedThreatsLabel;

    // Threat Alerts Table Components
    private DefaultTableModel tableModel;
    private JTable threatTable;
    private JComboBox<String> severityFilterCombo;

    // Explainer components
    private JLabel explainerScoreBadge;
    private JLabel explainerHeadlineLabel;
    private JTextArea explainerBreakdownArea;
    private JLabel explainerActionLabel;

    // 6 Sensors in Dashboard
    private final JLabel[] sensorStateLabels = new JLabel[6];
    private final JLabel[] sensorBadgeLabels = new JLabel[6];
    private final JButton[] sensorToggleButtons = new JButton[6];

    public SmartHomeSecurityApp() {
        setTitle("Smart Home Security Threat Monitor | Pure Java Application");
        setSize(1240, 840);
        setMinimumSize(new Dimension(1080, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DEEP);

        // Initialize SQLite Database via JDBC DAO
        DatabaseManager.initialize();
        eventsList.addAll(DatabaseManager.all());

        if (eventsList.isEmpty()) {
            seedSampleEvents();
        }

        initComponents();
        updateKpiSummary();
        refreshThreatTable();
        navigateTo("HOME");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top Navigation Bar (Website-style Navbar)
        add(createNavbar(), BorderLayout.NORTH);

        // Content Area with CardLayout
        contentPanel.setBackground(BG_MAIN);
        contentPanel.add(createHomePage(), "HOME");
        contentPanel.add(createDashboardPage(), "DASHBOARD");
        contentPanel.add(createThreatsPage(), "THREATS");
        contentPanel.add(createHowItWorksPage(), "HOW_IT_WORKS");
        contentPanel.add(createDetailsPage(), "DETAILS");
        contentPanel.add(createTeamPage(), "TEAM");

        add(contentPanel, BorderLayout.CENTER);

        // Bottom Footer Strip
        add(createFooterStrip(), BorderLayout.SOUTH);
    }

    // =========================================================================
    // WEBSITE-STYLE NAVBAR
    // =========================================================================
    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(BG_DEEP);
        navbar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Brand Logo & Title
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);

        JLabel iconLbl = new JLabel("🛡");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);

        JLabel title = new JLabel("Smart Home Threat Monitor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_PRIMARY);

        JLabel sub = new JLabel("Java 17 • SQLite JDBC • Heuristic Security Gateway");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(COLOR_CYAN);

        brandText.add(title);
        brandText.add(sub);

        brand.add(iconLbl);
        brand.add(brandText);
        navbar.add(brand, BorderLayout.WEST);

        // Nav Links in Center
        JPanel navLinks = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        navLinks.setOpaque(false);

        navLinks.add(createNavLink("Home", "HOME"));
        navLinks.add(createNavLink("Live Dashboard", "DASHBOARD"));
        navLinks.add(createNavLink("Threat Alerts", "THREATS"));
        navLinks.add(createNavLink("How It Works", "HOW_IT_WORKS"));
        navLinks.add(createNavLink("Project Details", "DETAILS"));
        navLinks.add(createNavLink("Team & Contact", "TEAM"));

        navbar.add(navLinks, BorderLayout.CENTER);

        // Right CTA Button
        JPanel ctaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ctaPanel.setOpaque(false);

        JButton ctaBtn = createStyledButton("View Dashboard", COLOR_BLUE, Color.WHITE);
        ctaBtn.addActionListener(e -> navigateTo("DASHBOARD"));

        JButton testBtn = createStyledButton("+ Simulate Event", BG_SURFACE, COLOR_CYAN);
        testBtn.addActionListener(e -> generateTestEvent());

        ctaPanel.add(testBtn);
        ctaPanel.add(ctaBtn);
        navbar.add(ctaPanel, BorderLayout.EAST);

        return navbar;
    }

    private JButton createNavLink(String text, String pageName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_DEEP);
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> navigateTo(pageName));
        navButtons.add(btn);
        return btn;
    }

    private void navigateTo(String pageName) {
        cardLayout.show(contentPanel, pageName);

        // Update active nav button state
        for (JButton b : navButtons) {
            String text = b.getText();
            boolean isActive = ("Home".equals(text) && "HOME".equals(pageName)) ||
                    ("Live Dashboard".equals(text) && "DASHBOARD".equals(pageName)) ||
                    ("Threat Alerts".equals(text) && "THREATS".equals(pageName)) ||
                    ("How It Works".equals(text) && "HOW_IT_WORKS".equals(pageName)) ||
                    ("Project Details".equals(text) && "DETAILS".equals(pageName)) ||
                    ("Team & Contact".equals(text) && "TEAM".equals(pageName));

            b.setForeground(isActive ? COLOR_CYAN : TEXT_MUTED);
            b.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 12));
        }
    }

    // =========================================================================
    // PAGE 1: HOME (HERO)
    // =========================================================================
    private JPanel createHomePage() {
        JPanel page = new JPanel(new BorderLayout(20, 20));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Center Hero Content
        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        // Badge
        JLabel badge = new JLabel(" Java Cybersecurity System • 100% Offline & Private ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(COLOR_CYAN);
        badge.setBackground(BG_CARD);
        badge.setOpaque(true);
        badge.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel heroTitle = new JLabel("Smart Home Security Threat Monitor");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heroTitle.setForeground(TEXT_PRIMARY);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description Paragraph
        JTextArea desc = new JTextArea(
                "An explainable, offline cybersecurity desktop application developed in Java. It monitors connected " +
                "entry sensors, environmental detectors, and gateway authentication logs in real-time—calculating dynamic " +
                "risk scores (0–100) and preventing unauthorized home intrusions without any cloud dependencies."
        );
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(TEXT_SECONDARY);
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setMaximumSize(new Dimension(840, 70));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hero CTA Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        btnPanel.setOpaque(false);

        JButton dashBtn = createStyledButton("View Live Dashboard", COLOR_BLUE, Color.WHITE);
        dashBtn.setPreferredSize(new Dimension(190, 42));
        dashBtn.addActionListener(e -> navigateTo("DASHBOARD"));

        JButton threatsBtn = createStyledButton("Check Threat Alerts", BG_CARD, TEXT_PRIMARY);
        threatsBtn.setPreferredSize(new Dimension(190, 42));
        threatsBtn.addActionListener(e -> navigateTo("THREATS"));

        JButton worksBtn = createStyledButton("How It Works", BG_SURFACE, COLOR_CYAN);
        worksBtn.setPreferredSize(new Dimension(160, 42));
        worksBtn.addActionListener(e -> navigateTo("HOW_IT_WORKS"));

        btnPanel.add(dashBtn);
        btnPanel.add(threatsBtn);
        btnPanel.add(worksBtn);
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        heroContent.add(Box.createVerticalStrut(10));
        heroContent.add(badge);
        heroContent.add(Box.createVerticalStrut(16));
        heroContent.add(heroTitle);
        heroContent.add(Box.createVerticalStrut(14));
        heroContent.add(desc);
        heroContent.add(Box.createVerticalStrut(24));
        heroContent.add(btnPanel);
        heroContent.add(Box.createVerticalStrut(30));

        // 4 Summary Feature Cards at bottom of Home
        JPanel summaryGrid = new JPanel(new GridLayout(1, 4, 14, 14));
        summaryGrid.setOpaque(false);
        summaryGrid.setMaximumSize(new Dimension(1100, 140));

        summaryGrid.add(createHomeSummaryCard("⚙ Core Architecture", "Java 17 (LTS)", "Modular OOP design with Java Swing GUI"));
        summaryGrid.add(createHomeSummaryCard("🔒 Anomaly Engine", "Explainable AI", "0–100 heuristic scoring based on time & subnets"));
        summaryGrid.add(createHomeSummaryCard("🗄 Local Persistence", "SQLite (JDBC)", "Zero-configuration security.db storage"));
        summaryGrid.add(createHomeSummaryCard("📡 Privacy Model", "100% Offline", "Zero cloud telemetry, completely private"));

        heroContent.add(summaryGrid);

        page.add(heroContent, BorderLayout.CENTER);
        return page;
    }

    private JPanel createHomeSummaryCard(String title, String val, String sub) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 4));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(COLOR_CYAN);

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));
        v.setForeground(TEXT_PRIMARY);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(TEXT_MUTED);

        card.add(t);
        card.add(v);
        card.add(s);
        return card;
    }

    // =========================================================================
    // PAGE 2: LIVE SECURITY DASHBOARD
    // =========================================================================
    private JPanel createDashboardPage() {
        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Top KPI Summary Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 14));
        kpiGrid.setOpaque(false);

        statusBadgeLabel = new JLabel("SECURE", SwingConstants.CENTER);
        statusBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusBadgeLabel.setForeground(COLOR_SAFE);

        totalEventsLabel = new JLabel("4 Events", SwingConstants.CENTER);
        totalEventsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalEventsLabel.setForeground(TEXT_PRIMARY);

        elevatedThreatsLabel = new JLabel("3 Threats", SwingConstants.CENTER);
        elevatedThreatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        elevatedThreatsLabel.setForeground(COLOR_WARNING);

        criticalEventsLabel = new JLabel("1 Alert", SwingConstants.CENTER);
        criticalEventsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        criticalEventsLabel.setForeground(COLOR_DANGER);

        kpiGrid.add(createKpiCard("SYSTEM STATUS", statusBadgeLabel, "Perimeter checks normal"));
        kpiGrid.add(createKpiCard("TOTAL EVENTS", totalEventsLabel, "Recorded in SQLite"));
        kpiGrid.add(createKpiCard("ELEVATED THREATS", elevatedThreatsLabel, "Medium, High, Critical"));
        kpiGrid.add(createKpiCard("CRITICAL ALERTS", criticalEventsLabel, "Risk Score >= 76/100"));

        page.add(kpiGrid, BorderLayout.NORTH);

        // Center Sensor Matrix
        JPanel sensorContainer = new JPanel(new BorderLayout(8, 8));
        sensorContainer.setOpaque(false);

        JLabel titleLbl = new JLabel("LIVE SENSOR TELEMETRY & ENTRY GUARDS");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(COLOR_CYAN);
        sensorContainer.add(titleLbl, BorderLayout.NORTH);

        JPanel sensorGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        sensorGrid.setOpaque(false);

        // 6 Sensors
        sensorGrid.add(createSensorCard(0, "Front Door Smart Lock", "Entrance • Door Sensor #01", "Closed & Deadbolt Engaged", "SAFE", COLOR_SAFE, "Simulate Door Breach"));
        sensorGrid.add(createSensorCard(1, "Living Room Window", "Ground Floor • Reed Switch #02", "Vibration Baseline 0.01g", "SAFE", COLOR_SAFE, "Simulate Glass Shock"));
        sensorGrid.add(createSensorCard(2, "Hallway PIR Motion", "Corridor • PIR Sensor #03", "Motion Detected (03:14 AM)", "WARNING", COLOR_WARNING, "Reset to Standby"));
        sensorGrid.add(createSensorCard(3, "Driveway Outdoor Camera", "Exterior East • Camera #04", "Active 1080p Stream (30 FPS)", "SAFE", COLOR_SAFE, "Simulate Video Loss"));
        sensorGrid.add(createSensorCard(4, "Kitchen Smoke & Gas Detector", "Utility • MQ-2 Gas #05", "14 ppm (Safe threshold < 50)", "SAFE", COLOR_SAFE, "Simulate Smoke Leak"));
        sensorGrid.add(createSensorCard(5, "Controller Gateway Hub", "Central Hub • Service #06", "Brute-Force Anomaly (10.99.0.15)", "DANGER", COLOR_DANGER, "Quarantine Offender IP"));

        sensorContainer.add(sensorGrid, BorderLayout.CENTER);
        page.add(sensorContainer, BorderLayout.CENTER);

        return page;
    }

    private JPanel createKpiCard(String title, JLabel valLabel, String subText) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(TEXT_MUTED);

        JLabel subLbl = new JLabel(subText);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLbl.setForeground(TEXT_DIM);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createSensorCard(int index, String name, String loc, String state, String status, Color color, String btnText) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(TEXT_PRIMARY);

        JLabel badgeLbl = new JLabel(" " + status + " ");
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeLbl.setOpaque(true);
        badgeLbl.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        badgeLbl.setForeground(color);
        badgeLbl.setBorder(new LineBorder(color, 1, true));
        sensorBadgeLabels[index] = badgeLbl;

        top.add(nameLbl, BorderLayout.WEST);
        top.add(badgeLbl, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2, 1, 0, 4));
        body.setOpaque(false);

        JLabel locLbl = new JLabel(loc);
        locLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        locLbl.setForeground(TEXT_MUTED);

        JLabel stateLbl = new JLabel(state);
        stateLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        stateLbl.setForeground(color);
        sensorStateLabels[index] = stateLbl;

        body.add(locLbl);
        body.add(stateLbl);
        card.add(body, BorderLayout.CENTER);

        JButton toggleBtn = new JButton(btnText);
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        toggleBtn.setBackground(BG_SURFACE);
        toggleBtn.setForeground(TEXT_PRIMARY);
        toggleBtn.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sensorToggleButtons[index] = toggleBtn;

        toggleBtn.addActionListener(e -> handleSensorToggle(index));
        card.add(toggleBtn, BorderLayout.SOUTH);

        return card;
    }

    // =========================================================================
    // PAGE 3: THREAT ALERTS & INCIDENT HISTORY
    // =========================================================================
    private JPanel createThreatsPage() {
        JPanel page = new JPanel(new BorderLayout(12, 12));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        toolbar.setBackground(BG_CARD);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton simBtn = createStyledButton("+ Simulate Test Event", COLOR_CYAN, Color.BLACK);
        simBtn.addActionListener(e -> generateTestEvent());

        JButton exportBtn = createStyledButton("Export CSV Report", TEXT_PRIMARY, BG_SURFACE);
        exportBtn.addActionListener(e -> exportCsv());

        JButton resetBtn = createStyledButton("Reset Demo Events", TEXT_MUTED, BG_SURFACE);
        resetBtn.addActionListener(e -> resetEvents());

        JLabel filterLbl = new JLabel("Severity:");
        filterLbl.setForeground(TEXT_MUTED);
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));

        severityFilterCombo = new JComboBox<>(new String[]{"ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW"});
        severityFilterCombo.setBackground(BG_SURFACE);
        severityFilterCombo.setForeground(TEXT_PRIMARY);
        severityFilterCombo.addActionListener(e -> refreshThreatTable());

        toolbar.add(simBtn);
        toolbar.add(exportBtn);
        toolbar.add(resetBtn);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(filterLbl);
        toolbar.add(severityFilterCombo);

        page.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Timestamp", "Threat Level", "Risk Score", "Event Type", "User", "IP Address", "Suggested Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        threatTable = new JTable(tableModel);
        threatTable.setBackground(BG_CARD);
        threatTable.setForeground(TEXT_PRIMARY);
        threatTable.setGridColor(BORDER_COLOR);
        threatTable.setRowHeight(28);
        threatTable.setSelectionBackground(new Color(56, 189, 248, 45));
        threatTable.setSelectionForeground(COLOR_CYAN);
        threatTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTableHeader th = threatTable.getTableHeader();
        th.setBackground(BG_SURFACE);
        th.setForeground(TEXT_MUTED);
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBorder(new LineBorder(BORDER_COLOR));

        threatTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String val = String.valueOf(value);
                if ("CRITICAL".equals(val)) lbl.setForeground(COLOR_DANGER);
                else if ("HIGH".equals(val)) lbl.setForeground(COLOR_WARNING);
                else if ("MEDIUM".equals(val)) lbl.setForeground(COLOR_CYAN);
                else lbl.setForeground(COLOR_SAFE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                return lbl;
            }
        });

        threatTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = threatTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = threatTable.convertRowIndexToModel(selectedRow);
                int id = (int) tableModel.getValueAt(modelRow, 0);
                updateExplainerBox(id);
            }
        });

        JScrollPane scrollPane = new JScrollPane(threatTable);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        page.add(scrollPane, BorderLayout.CENTER);

        // Bottom Explainable AI Box
        page.add(createExplainerPanel(), BorderLayout.SOUTH);

        return page;
    }

    private JPanel createExplainerPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_CYAN, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel titleLbl = new JLabel("EXPLAINABLE ANOMALY RISK BREAKDOWN (ThreatDetector)");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(COLOR_CYAN);

        explainerScoreBadge = new JLabel("Risk: 85/100 (CRITICAL)");
        explainerScoreBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        explainerScoreBadge.setForeground(COLOR_DANGER);

        top.add(titleLbl, BorderLayout.WEST);
        top.add(explainerScoreBadge, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        explainerBreakdownArea = new JTextArea(4, 50);
        explainerBreakdownArea.setEditable(false);
        explainerBreakdownArea.setBackground(BG_DEEP);
        explainerBreakdownArea.setForeground(TEXT_PRIMARY);
        explainerBreakdownArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        explainerBreakdownArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        panel.add(explainerBreakdownArea, BorderLayout.CENTER);

        explainerActionLabel = new JLabel("Homeowner Mitigation: Select any row above to inspect heuristic rules.");
        explainerActionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        explainerActionLabel.setForeground(TEXT_MUTED);
        panel.add(explainerActionLabel, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // PAGE 4: HOW IT WORKS
    // =========================================================================
    private JPanel createHowItWorksPage() {
        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("HOW THE SYSTEM WORKS — 4-STEP PIPELINE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(COLOR_CYAN);
        page.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        grid.add(createStepCard("STEP 1: SENSOR INGESTION",
                "Sensors (doors, windows, motion, smoke) detect state changes.\n" +
                "In our project, LogSimulator.java and SensorMonitorThread safely generate\n" +
                "and stream simulated events without risk to physical hardware.",
                "Service: com.security.service"));

        grid.add(createStepCard("STEP 2: RULE-BASED ANOMALY SCORING",
                "ThreatDetector.java evaluates event type penalties (+20 to +60),\n" +
                "repeated IP failures (+10 per streak), untrusted subnets 10.99.x (+15),\n" +
                "and off-hours timing (< 6 AM or >= 11 PM) (+15) to calculate a 0–100 score.",
                "Engine: com.security.detector"));

        grid.add(createStepCard("STEP 3: LOCAL SQLITE JDBC PERSISTENCE",
                "DatabaseManager.java writes analyzed incidents to security.db using\n" +
                "parameterized PreparedStatement queries, creating a persistent audit\n" +
                "trail that survives application restarts.",
                "Persistence: com.security.database"));

        grid.add(createStepCard("STEP 4: REAL-TIME DESKTOP ALERTS",
                "The Java Swing desktop interface updates live totals, triggers\n" +
                "modal alerts for HIGH/CRITICAL events, and exports compliance reports\n" +
                "to security_report.csv.",
                "Presentation: com.security.ui"));

        page.add(grid, BorderLayout.CENTER);
        return page;
    }

    private JPanel createStepCard(String header, String desc, String pkg) {
        JPanel c = new JPanel(new BorderLayout(6, 6));
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel h = new JLabel(header);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setForeground(TEXT_PRIMARY);

        JTextArea d = new JTextArea(desc);
        d.setEditable(false);
        d.setOpaque(false);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(TEXT_MUTED);

        JLabel p = new JLabel(pkg);
        p.setFont(new Font("Consolas", Font.BOLD, 11));
        p.setForeground(COLOR_CYAN);

        c.add(h, BorderLayout.NORTH);
        c.add(d, BorderLayout.CENTER);
        c.add(p, BorderLayout.SOUTH);
        return c;
    }

    // =========================================================================
    // PAGE 5: PROJECT DETAILS & DB SCHEMA
    // =========================================================================
    private JPanel createDetailsPage() {
        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("TECHNICAL SPECIFICATIONS & DATABASE SCHEMA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(COLOR_CYAN);
        page.add(title, BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 16, 16));
        split.setOpaque(false);

        // Left Schema Box
        JPanel schemaPanel = new JPanel(new BorderLayout(8, 8));
        schemaPanel.setBackground(BG_CARD);
        schemaPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel sTitle = new JLabel("SQLite Database Schema: security_events (security.db)");
        sTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sTitle.setForeground(COLOR_CYAN);
        schemaPanel.add(sTitle, BorderLayout.NORTH);

        JTextArea sqlArea = new JTextArea(
                "CREATE TABLE security_events (\n" +
                "    id           INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    timestamp    TEXT NOT NULL,\n" +
                "    username     TEXT NOT NULL,\n" +
                "    ip_address   TEXT NOT NULL,\n" +
                "    event_type   TEXT NOT NULL,\n" +
                "    description  TEXT NOT NULL,\n" +
                "    threat_level TEXT NOT NULL,\n" +
                "    risk_score   INTEGER NOT NULL\n" +
                ");"
        );
        sqlArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        sqlArea.setBackground(BG_DEEP);
        sqlArea.setForeground(COLOR_CYAN);
        sqlArea.setEditable(false);
        schemaPanel.add(new JScrollPane(sqlArea), BorderLayout.CENTER);
        split.add(schemaPanel);

        // Right Hardware Placeholders
        JPanel hwPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        hwPanel.setBackground(BG_CARD);
        hwPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        hwPanel.add(createPlaceholderRow("Central Controller Hub", "Raspberry Pi 4 Model B (4GB) or Local Java Workstation"));
        hwPanel.add(createPlaceholderRow("Connected Sensor Modules", "Magnetic reed switches, PIR HC-SR501, MQ-2 Optical Smoke"));
        hwPanel.add(createPlaceholderRow("IoT Communication Bus", "Local socket connection / Mosquitto MQTT broker / Serial GPIO"));
        hwPanel.add(createPlaceholderRow("Database Scalability", "SQLite 3 default / Optional PostgreSQL connector"));
        split.add(hwPanel);

        page.add(split, BorderLayout.CENTER);
        return page;
    }

    private JPanel createPlaceholderRow(String tag, String text) {
        JPanel r = new JPanel(new GridLayout(2, 1, 0, 2));
        r.setOpaque(false);
        JLabel t = new JLabel(tag);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(COLOR_CYAN);
        JLabel d = new JLabel(text);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        d.setForeground(TEXT_MUTED);
        r.add(t);
        r.add(d);
        return r;
    }

    // =========================================================================
    // PAGE 6: TEAM & CONTACT
    // =========================================================================
    private JPanel createTeamPage() {
        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("PROJECT CONTRIBUTORS & CONTACT DETAILS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(COLOR_CYAN);
        page.add(title, BorderLayout.NORTH);

        JPanel teamGrid = new JPanel(new GridLayout(1, 4, 14, 14));
        teamGrid.setOpaque(false);

        teamGrid.add(createMemberCard("Member 1", "Database & Persistence Lead", "Roll No: [Enter Roll No]", "email1@institution.edu"));
        teamGrid.add(createMemberCard("Member 2", "Cybersecurity & Anomaly Engine", "Roll No: [Enter Roll No]", "email2@institution.edu"));
        teamGrid.add(createMemberCard("Member 3", "Desktop GUI & Visualization", "Roll No: [Enter Roll No]", "email3@institution.edu"));
        teamGrid.add(createMemberCard("Member 4", "OOP Architecture & Testing", "Roll No: [Enter Roll No]", "email4@institution.edu"));

        page.add(teamGrid, BorderLayout.CENTER);

        // Academic details
        JPanel bottom = new JPanel(new GridLayout(1, 2, 14, 14));
        bottom.setBackground(BG_CARD);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JTextArea subDetails = new JTextArea(
                "ACADEMIC SUBMISSION DETAILS:\n" +
                "• Project Title: Smart Home Security Threat Monitor\n" +
                "• Department: Department of Computer Science & Engineering\n" +
                "• Institution: [College / University Name]\n" +
                "• Academic Guide: [Professor / Faculty Advisor]\n" +
                "• Core Stack: Java 17 (LTS), Java Swing, SQLite JDBC"
        );
        subDetails.setEditable(false);
        subDetails.setOpaque(false);
        subDetails.setForeground(TEXT_PRIMARY);
        subDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton sendFeedback = createStyledButton("Record Project Notes", COLOR_CYAN, Color.BLACK);
        sendFeedback.addActionListener(e -> {
            String note = JOptionPane.showInputDialog(this, "Enter faculty remarks or project feedback:", "Project Inquiry", JOptionPane.PLAIN_MESSAGE);
            if (note != null && !note.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Thank you! Notes have been recorded.", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        bottom.add(subDetails);
        bottom.add(sendFeedback);

        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private JPanel createMemberCard(String name, String role, String roll, String email) {
        JPanel c = new JPanel(new GridLayout(4, 1, 0, 4));
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 14, 16, 14)
        ));

        JLabel n = new JLabel(name);
        n.setFont(new Font("Segoe UI", Font.BOLD, 14));
        n.setForeground(TEXT_PRIMARY);

        JLabel r = new JLabel(role);
        r.setFont(new Font("Segoe UI", Font.BOLD, 11));
        r.setForeground(COLOR_CYAN);

        JLabel rl = new JLabel(roll);
        rl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rl.setForeground(TEXT_MUTED);

        JLabel em = new JLabel(email);
        em.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        em.setForeground(TEXT_DIM);

        c.add(n);
        c.add(r);
        c.add(rl);
        c.add(em);
        return c;
    }

    // =========================================================================
    // FOOTER STRIP
    // =========================================================================
    private JPanel createFooterStrip() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_DEEP);
        footer.setBorder(new EmptyBorder(8, 20, 8, 20));

        JLabel copy = new JLabel("© 2026 Smart Home Security Threat Monitor • Java Cybersecurity Application");
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copy.setForeground(TEXT_DIM);

        JLabel status = new JLabel("System Status: All Core Services Operational (SQLite Connected)  ");
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(COLOR_SAFE);

        footer.add(copy, BorderLayout.WEST);
        footer.add(status, BorderLayout.EAST);
        return footer;
    }

    // =========================================================================
    // BUSINESS LOGIC & EVENT HANDLERS
    // =========================================================================
    private void generateTestEvent() {
        SecurityEvent event = simulator.next();
        detector.analyze(event);

        try {
            DatabaseManager.save(event);
        } catch (Exception ignored) {}

        eventsList.add(0, event);
        refreshThreatTable();
        updateKpiSummary();
        updateExplainerBox(event.getId() != null ? event.getId() : 0);

        if (event.getThreatLevel() == ThreatLevel.HIGH || event.getThreatLevel() == ThreatLevel.CRITICAL) {
            JOptionPane.showMessageDialog(this,
                    "POTENTIAL SECURITY THREAT DETECTED!\n\n" +
                    "Threat Level: " + event.getThreatLevel() + "\n" +
                    "Event Type: " + event.getEventType() + "\n" +
                    "Source IP: " + event.getIpAddress() + " (User: " + event.getUsername() + ")\n" +
                    "Risk Score: " + event.getRiskScore() + " / 100\n\n" +
                    "Explanation: " + detector.explain(event),
                    "Security Threat Alert",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSensorToggle(int index) {
        JLabel state = sensorStateLabels[index];
        JLabel badge = sensorBadgeLabels[index];
        JButton btn = sensorToggleButtons[index];

        if (index == 0) { // Front Door
            if ("SAFE".equals(badge.getText().trim())) {
                badge.setText(" DANGER ");
                badge.setForeground(COLOR_DANGER);
                state.setText("Forced Open / Contact Broken");
                state.setForeground(COLOR_DANGER);
                btn.setText("Restore Door");
                generateTestEvent();
            } else {
                badge.setText(" SAFE ");
                badge.setForeground(COLOR_SAFE);
                state.setText("Closed & Deadbolt Engaged");
                state.setForeground(COLOR_SAFE);
                btn.setText("Simulate Door Breach");
            }
        } else if (index == 1) { // Window
            if ("SAFE".equals(badge.getText().trim())) {
                badge.setText(" DANGER ");
                badge.setForeground(COLOR_DANGER);
                state.setText("Acoustic Glass Shock (2.4g Spike)");
                state.setForeground(COLOR_DANGER);
                btn.setText("Restore Window");
                generateTestEvent();
            } else {
                badge.setText(" SAFE ");
                badge.setForeground(COLOR_SAFE);
                state.setText("Vibration Baseline 0.01g");
                state.setForeground(COLOR_SAFE);
                btn.setText("Simulate Glass Shock");
            }
        } else if (index == 2) { // Motion
            if ("WARNING".equals(badge.getText().trim())) {
                badge.setText(" SAFE ");
                badge.setForeground(COLOR_SAFE);
                state.setText("Standby (No Movement)");
                state.setForeground(COLOR_SAFE);
                btn.setText("Simulate Off-Hours Motion");
            } else {
                badge.setText(" WARNING ");
                badge.setForeground(COLOR_WARNING);
                state.setText("Motion Detected (03:14 AM)");
                state.setForeground(COLOR_WARNING);
                btn.setText("Reset to Standby");
            }
        } else if (index == 3) { // Camera
            if ("SAFE".equals(badge.getText().trim())) {
                badge.setText(" WARNING ");
                badge.setForeground(COLOR_WARNING);
                state.setText("RTSP Video Loss (Feed Offline)");
                state.setForeground(COLOR_WARNING);
                btn.setText("Reconnect Camera");
            } else {
                badge.setText(" SAFE ");
                badge.setForeground(COLOR_SAFE);
                state.setText("Active 1080p Stream (30 FPS)");
                state.setForeground(COLOR_SAFE);
                btn.setText("Simulate Video Loss");
            }
        } else if (index == 4) { // Smoke
            if ("SAFE".equals(badge.getText().trim())) {
                badge.setText(" DANGER ");
                badge.setForeground(COLOR_DANGER);
                state.setText("High Smoke Density (68 ppm)");
                state.setForeground(COLOR_DANGER);
                btn.setText("Clear Smoke Alarm");
                generateTestEvent();
            } else {
                badge.setText(" SAFE ");
                badge.setForeground(COLOR_SAFE);
                state.setText("14 ppm (Safe threshold < 50)");
                state.setForeground(COLOR_SAFE);
                btn.setText("Simulate Smoke Leak");
            }
        } else if (index == 5) { // Gateway
            if ("DANGER".equals(badge.getText().trim())) {
                badge.setText(" SAFE ");
                badge.setForeground(COLOR_SAFE);
                state.setText("Firewall Active • IP 10.99.0.15 Quarantined");
                state.setForeground(COLOR_SAFE);
                btn.setText("Remove IP Quarantine");
                JOptionPane.showMessageDialog(this, "Firewall Rule Applied: IP 10.99.0.15 dropped from gateway access list.", "Firewall Update", JOptionPane.INFORMATION_MESSAGE);
            } else {
                badge.setText(" DANGER ");
                badge.setForeground(COLOR_DANGER);
                state.setText("Brute-Force Anomaly (10.99.0.15)");
                state.setForeground(COLOR_DANGER);
                btn.setText("Quarantine Offender IP");
            }
        }
    }

    private void refreshThreatTable() {
        tableModel.setRowCount(0);
        String selectedFilter = (String) severityFilterCombo.getSelectedItem();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < eventsList.size(); i++) {
            SecurityEvent e = eventsList.get(i);
            if (!"ALL".equals(selectedFilter) && !e.getThreatLevel().name().equals(selectedFilter)) {
                continue;
            }

            int id = e.getId() != null ? e.getId() : (i + 1);
            String timeStr = e.getTimestamp() != null ? e.getTimestamp().format(dtf) : "Now";
            String action = getSuggestedAction(e);

            tableModel.addRow(new Object[]{
                    id,
                    timeStr,
                    e.getThreatLevel().name(),
                    e.getRiskScore() + "/100",
                    e.getEventType(),
                    e.getUsername(),
                    e.getIpAddress(),
                    action
            });
        }

        if (threatTable.getRowCount() > 0 && threatTable.getSelectedRow() == -1) {
            threatTable.setRowSelectionInterval(0, 0);
            updateExplainerBox((int) tableModel.getValueAt(0, 0));
        }
    }

    private void updateExplainerBox(int eventId) {
        SecurityEvent event = null;
        for (SecurityEvent e : eventsList) {
            if (e.getId() != null && e.getId() == eventId) {
                event = e;
                break;
            }
        }

        if (event == null && !eventsList.isEmpty()) {
            event = eventsList.get(0);
        }

        if (event != null) {
            explainerScoreBadge.setText(String.format("Risk: %d/100 (%s)", event.getRiskScore(), event.getThreatLevel()));
            explainerScoreBadge.setForeground(
                    event.getThreatLevel() == ThreatLevel.CRITICAL ? COLOR_DANGER :
                    event.getThreatLevel() == ThreatLevel.HIGH ? COLOR_WARNING :
                    event.getThreatLevel() == ThreatLevel.MEDIUM ? COLOR_CYAN : COLOR_SAFE
            );

            int basePoints = event.getEventType().equals("LOGIN_FAILED") ? 20 :
                             event.getEventType().equals("UNKNOWN_USER") ? 45 :
                             event.getEventType().equals("SUSPICIOUS_ACCESS") ? 60 : 5;

            boolean isUntrustedSubnet = event.getIpAddress().startsWith("10.99.");
            int hour = event.getTimestamp().getHour();
            boolean isOffHours = hour < 6 || hour >= 23;

            explainerBreakdownArea.setText(String.format(
                    "• Base Event Classification Penalty: +%d (%s)\n" +
                    "• Subnet Anomaly Check: %s\n" +
                    "• Off-Hours Temporal Check (< 06:00 or >= 23:00): %s\n" +
                    "• Rule Explanation: %s",
                    basePoints, event.getEventType(),
                    isUntrustedSubnet ? "+15 (Untrusted External Subnet 10.99.x.x)" : "+0 (Trusted Home Subnet)",
                    isOffHours ? "+15 (Night-time access detected)" : "+0 (Daytime)",
                    detector.explain(event)
            ));

            explainerActionLabel.setText("Homeowner Mitigation: " + getSuggestedAction(event));
        }
    }

    private String getSuggestedAction(SecurityEvent e) {
        if (e.getThreatLevel() == ThreatLevel.CRITICAL) {
            return "Quarantine IP " + e.getIpAddress() + " on firewall & verify credentials";
        } else if (e.getThreatLevel() == ThreatLevel.HIGH) {
            return "Inspect entry sensors and rate-limit access attempts";
        } else if (e.getThreatLevel() == ThreatLevel.MEDIUM) {
            return "Monitor IP " + e.getIpAddress() + " for repeat mistypes";
        } else {
            return "Routine authorized telemetry. No action needed.";
        }
    }

    private void updateKpiSummary() {
        long total = eventsList.size();
        long threats = eventsList.stream().filter(e -> e.getThreatLevel() != ThreatLevel.LOW).count();
        long critical = eventsList.stream().filter(e -> e.getThreatLevel() == ThreatLevel.CRITICAL).count();

        totalEventsLabel.setText(total + " Events");
        elevatedThreatsLabel.setText(threats + " Threats");
        criticalEventsLabel.setText(critical + " Alert" + (critical == 1 ? "" : "s"));

        if (critical > 0) {
            statusBadgeLabel.setText("ATTENTION");
            statusBadgeLabel.setForeground(COLOR_DANGER);
        } else if (threats > 3) {
            statusBadgeLabel.setText("WARNING");
            statusBadgeLabel.setForeground(COLOR_WARNING);
        } else {
            statusBadgeLabel.setText("SECURE");
            statusBadgeLabel.setForeground(COLOR_SAFE);
        }
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("security_report.csv"));
        int res = fileChooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("Timestamp,Username,IP,Event,Description,Threat,RiskScore");
                for (SecurityEvent e : eventsList) {
                    pw.printf("%s,%s,%s,%s,\"%s\",%s,%d%n",
                            e.getTimestamp().format(dtf),
                            e.getUsername(),
                            e.getIpAddress(),
                            e.getEventType(),
                            e.getDescription(),
                            e.getThreatLevel().name(),
                            e.getRiskScore());
                }
                JOptionPane.showMessageDialog(this, "Security report exported successfully to:\n" + file.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetEvents() {
        eventsList.clear();
        seedSampleEvents();
        refreshThreatTable();
        updateKpiSummary();
        JOptionPane.showMessageDialog(this, "Sample events restored to initial baseline.", "Reset Done", JOptionPane.INFORMATION_MESSAGE);
    }

    private void seedSampleEvents() {
        LocalDateTime now = LocalDateTime.now();
        eventsList.add(new AuthSecurityEvent(now.minusMinutes(4), "admin", "10.99.0.15", "SUSPICIOUS_ACCESS", "Unusual access pattern on central gateway", 3, false));
        eventsList.get(0).setThreatLevel(ThreatLevel.CRITICAL);
        eventsList.get(0).setRiskScore(85);

        eventsList.add(new AuthSecurityEvent(now.minusMinutes(14), "unknown", "10.99.0.15", "UNKNOWN_USER", "Login attempted with an unknown account profile", 1, false));
        eventsList.get(1).setThreatLevel(ThreatLevel.HIGH);
        eventsList.get(1).setRiskScore(60);

        eventsList.add(new SensorSecurityEvent(now.minusMinutes(28), "student", "10.0.0.8", "LOGIN_FAILED", "Invalid password / failed login attempt", "Study Room", "WiFi"));
        eventsList.get(2).setThreatLevel(ThreatLevel.MEDIUM);
        eventsList.get(2).setRiskScore(35);

        eventsList.add(new SensorSecurityEvent(now.minusMinutes(55), "admin", "192.168.1.5", "LOGIN_SUCCESS", "Successful authentication on smart hub console", "Master Console", "Ethernet"));
        eventsList.get(3).setThreatLevel(ThreatLevel.LOW);
        eventsList.get(3).setRiskScore(5);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new SmartHomeSecurityApp().setVisible(true);
        });
    }
}
