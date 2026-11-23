package tn.jakloc.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ADMIN console; renders sample rental requests from request attributes (swap for real data later, JSP untouched).
@WebServlet(name = "AdminDashboardServlet", urlPatterns = AppConstants.ADMIN_DASHBOARD_PATH)
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW = "/WEB-INF/views/admin/dashboard.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Map<String, Object>> requests = sampleRequests();
        request.setAttribute("activeFilter", "all");
        request.setAttribute("stats", statsFrom(requests));
        request.setAttribute("requests", requests);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    // Counts derived from the rows, so the stat cards and pills can never disagree with the table.
    private Map<String, String> statsFrom(List<Map<String, Object>> rows) {
        int pending = 0;
        int validated = 0;
        int flagged = 0;
        for (Map<String, Object> row : rows) {
            String bucket = String.valueOf(row.get("statusState"));
            if ("pending".equals(bucket)) {
                pending++;
            } else if ("validated".equals(bucket)) {
                validated++;
            } else if ("flagged".equals(bucket)) {
                flagged++;
            }
        }
        Map<String, String> stats = new LinkedHashMap<>();
        stats.put("pending", Integer.toString(pending));
        stats.put("validated", Integer.toString(validated));
        stats.put("flagged", Integer.toString(flagged));
        stats.put("total", Integer.toString(rows.size()));
        return stats;
    }

    // One sample request; the keys below are the view contract the JSP reads.
    private Map<String, Object> row(String id, String initials, String name, String vehicle,
            String vehicleMeta, String dateRange, String days, String price, String holdLabel,
            String holdState, String statusLabel, String statusState,
            List<Map<String, String>> docs, List<Map<String, String>> actions) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("initials", initials);
        m.put("name", name);
        m.put("vehicle", vehicle);
        m.put("vehicleMeta", vehicleMeta);
        m.put("dateRange", dateRange);
        m.put("days", days);
        m.put("price", price);
        m.put("holdLabel", holdLabel);
        m.put("holdState", holdState); // ok | pending
        m.put("statusLabel", statusLabel);
        m.put("statusState", statusState); // pending | validated | flagged
        m.put("docs", docs);
        m.put("actions", actions);
        return m;
    }

    private Map<String, String> doc(String label, String value, String state) {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("label", label);
        d.put("value", value);
        d.put("state", state); // ok | warn | bad
        return d;
    }

    private Map<String, String> action(String label, String icon, String kind) {
        Map<String, String> a = new LinkedHashMap<>();
        a.put("label", label);
        a.put("icon", icon);
        a.put("kind", kind); // primary | ghost | danger
        return a;
    }

    private List<Map<String, Object>> sampleRequests() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(row("RX-8821", "SJ", "Sarah Jenkins",
                "Porsche 911 GT3 (992)", "Chalk Grey \u00b7 502 HP \u00b7 PDK",
                "Oct 24 - Oct 27", "3d", "$2,850", "$5,000 Auth Hold", "ok",
                "Needs Manual Review", "pending",
                List.of(doc("DL", "Clear", "ok"), doc("COI", "Verified", "ok"), doc("Age", "26", "ok")),
                List.of(action("Review & Approve", "check", "primary"), action("Reject", "block", "danger"))));
        list.add(row("RX-8822", "AM", "Alexandre Moreau",
                "Lamborghini Urus S", "Nero Nemesis \u00b7 657 HP \u00b7 AWD",
                "Oct 25 - Oct 29", "4d", "$5,400", "$7,500 Auth Hold", "ok",
                "Auto-Cleared", "validated",
                List.of(doc("Passport", "Match", "ok"), doc("Excess", "Paid", "ok")),
                List.of(action("Issue Keypack", "key", "primary"), action("Refuse", "block", "danger"))));
        list.add(row("RX-8825", "MV", "Marcus Vance",
                "BMW M3 Competition", "Isle of Man Green \u00b7 503 HP",
                "Oct 26 - Oct 28", "2d", "$1,380", "Deposit Pending", "pending",
                "Flagged Docs", "flagged",
                List.of(doc("ID", "Valid", "ok"), doc("COI", "Lapsed", "bad")),
                List.of(action("Request Policy", "mail", "ghost"), action("Reject", "block", "danger"))));
        list.add(row("RX-8828", "ER", "Elena Rostova",
                "Rolls-Royce Ghost", "V12 Diamond Black \u00b7 Bespoke Audio",
                "Oct 27 - Nov 02", "6d", "$8,820", "$10,000 Auth Hold", "ok",
                "Ready for Dispatch", "validated",
                List.of(doc("VIP Clearance", "Granted", "ok")),
                List.of(action("Chauffeur Release", "person", "primary"), action("Refuse", "block", "danger"))));
        return list;
    }
}