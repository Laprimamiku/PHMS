<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*,java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>收藏可视化分析</title>
  <script src="https://cdn.jsdelivr.net/npm/chart.js@3.7.0/dist/chart.min.js"></script>
  <link rel="stylesheet" href="css/fav4.css">
  <style>
    .header-buttons {
      display: flex;
      gap: 10px;
    }
    .recommendation-button {
      display: inline-block;
      padding: 8px 15px;
      background: #4CAF50;
      color: white;
      text-decoration: none;
      border-radius: 4px;
    }
    .recommendation-button:hover {
      background: #45a049;
    }
  </style>
</head>
<body>
  <div class="page-header">
    <h1>收藏夹可视化分析</h1>
    <div class="header-buttons">
      <a class="recommendation-button" href="recommendation">查看推荐</a>
      <a class="return-button" href="FavoriteServlet?action=viewFolders">返回收藏</a>
    </div>
  </div>

  <div class="filter-container">
    <select id="timeRange">
      <option value="3">最近3天</option>
      <option value="7">最近一周</option>
      <option value="30">最近一月</option>
      <option value="all" selected>全部时间</option>
    </select>
  </div>

  <div class="stats-cards">
    <div class="stat-card"><h3>总收藏数</h3><p id="totalFavorites">-</p></div>
    <div class="stat-card"><h3>收藏类型数</h3><p id="uniqueTypes">-</p></div>
    <div class="stat-card"><h3>平均每日收藏</h3><p id="avgDaily">-</p></div>
  </div>

  <div class="charts-wrapper">
    <div class="chart-container">
      <h2>收藏时间趋势（折线图）</h2>
      <canvas id="timeTrendChart"></canvas>
    </div>
    <div class="chart-container">
      <h2>宠物类型分布（柱状图）</h2>
      <canvas id="typeBarChart"></canvas>
    </div>
    <div class="chart-container">
      <h2>宠物类型占比（饼图）</h2>
      <canvas id="typePieChart"></canvas>
    </div>
  </div>

  <script>
    // 后端数据准备
    <% 
      List<Map<String,Object>> tt = (List<Map<String,Object>>)request.getAttribute("timeTrend");
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      StringJoiner sjDates = new StringJoiner(","), sjCounts = new StringJoiner(",");
      for (Map<String,Object> m : tt) {
        sjDates.add("\""+df.format(m.get("date"))+"\"");
        sjCounts.add(m.get("count").toString());
      }
      List<Map<String,Object>> td = (List<Map<String,Object>>)request.getAttribute("typeDist");
      StringJoiner sjTypes = new StringJoiner(","), sjTypeCnts = new StringJoiner(",");
      for (Map<String,Object> m : td) {
        sjTypes.add("\""+m.get("type")+"\"");
        sjTypeCnts.add(m.get("cnt").toString());
      }
    %>
    const data = {
      dates: [<%= sjDates %>],
      counts: [<%= sjCounts %>],
      types: [<%= sjTypes %>],
      typeCounts: [<%= sjTypeCnts %>]
    };

    // 更新统计卡片
    function updateStats() {
      const total = data.counts.reduce((a, b) => a + b, 0);
      document.getElementById('totalFavorites').textContent = total;
      document.getElementById('uniqueTypes').textContent = data.types.length;
      document.getElementById('avgDaily').textContent = (total / (data.dates.length || 1)).toFixed(1);
    }

    // 通用图表生成
    function createChart(ctx, config) {
      return new Chart(ctx, config);
    }

    // 配置生成
    const configs = {
      line: {
        type: 'line',
        data: {
          labels: data.dates,
          datasets: [{
            label: '收藏量',
            data: data.counts,
            borderColor: 'rgba(54, 162, 235, 1)',
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            tension: 0.2,
            fill: true
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          aspectRatio: 2,
          plugins: {
            tooltip: {            // 启用 Tooltip，悬停显示数据
              enabled: true,
              mode: 'index',
              intersect: false
            },
            legend: { labels: { font: { size: 14 } } }
          },
          interaction: {
            mode: 'nearest',
            axis: 'x',
            intersect: false
          },
          scales: {
            x: { ticks: { font: { size: 14 } } },
            y: { beginAtZero: true, ticks: { font: { size: 14 } } }
          }
        }
      },
      bar: {
        type: 'bar',
        data: {
          labels: data.types,
          datasets: [{
            label: '收藏量',
            data: data.typeCounts,
            backgroundColor: 'rgba(75, 192, 192, 0.6)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          aspectRatio: 2,
          plugins: { legend: { display: false } },
          scales: {
            x: { ticks: { font: { size: 14 } } },
            y: { beginAtZero: true, ticks: { font: { size: 14 } } }
          }
        }
      },
      pie: {
        type: 'pie',
        data: {
          labels: data.types,
          datasets: [{
            data: data.typeCounts,
            backgroundColor: [
              'rgba(255, 99, 132, 0.8)', 'rgba(255, 206, 86, 0.8)',
              'rgba(54, 162, 235, 0.8)', 'rgba(75, 192, 192, 0.8)',
              'rgba(153, 102, 255, 0.8)', 'rgba(255, 159, 64, 0.8)'
            ]
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          aspectRatio: 1
        }
      }
    };

    // 初始化图表与交互
    document.addEventListener('DOMContentLoaded', () => {
      updateStats();
      const ctxTime = document.getElementById('timeTrendChart').getContext('2d');
      const ctxBar  = document.getElementById('typeBarChart').getContext('2d');
      const ctxPie  = document.getElementById('typePieChart').getContext('2d');

      const chartTime = createChart(ctxTime, configs.line);
      createChart(ctxBar, configs.bar);
      createChart(ctxPie, configs.pie);

      // 时间筛选
      document.getElementById('timeRange').addEventListener('change', e => {
        const val = e.target.value;
        let labs = data.dates, ds = data.counts;
        if (val !== 'all') {
          const cutoff = new Date();
          cutoff.setDate(cutoff.getDate() - parseInt(val));
          const filtered = labs.map((d, i) => ({ d: new Date(d), c: ds[i] }))
                                .filter(x => x.d >= cutoff);
          labs = filtered.map(x => x.d.toISOString().slice(0, 10));
          ds  = filtered.map(x => x.c);
        }
        chartTime.data.labels = labs;
        chartTime.data.datasets[0].data = ds;
        chartTime.update();
      });
    });
  </script>
</body>
</html>
