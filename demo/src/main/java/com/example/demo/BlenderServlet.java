package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BlenderServlet extends HttpServlet {

    private static class Color {
        double r, g, b;

        private Color(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public static Color black() {
            return new Color(0, 0, 0);
        }

        public void add(Color other) {
            r += other.r;
            g += other.g;
            b += other.b;
        }

        public void add(double nr, double ng, double nb) {
            r += nr;
            g += ng;
            b += nb;
        }

        public void multiply(double factor) {
            r *= factor;
            g *= factor;
            b *= factor;
        }
    }

    private static final Color[][][] colors = new Color[100][100][100];

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");

        long totalStartTime = System.nanoTime(); // 전체 실행 시간 시작
        long totalLoopTime = 0; // 모든 루프의 실행 시간을 누적할 변수

        for (int j = 0; j < 10; j++) {
            long t = System.nanoTime(); // 루프 시작 시간 측정
            for (int i = 0; i < 100; i++) {
                initialize(new Color(j / 20.0, 0, 1));
            }
            long d = System.nanoTime() - t; // 루프 종료 시간 측정
            totalLoopTime += d;

            // 클라이언트로 루프당 소요 시간을 전송
            String data = "data: 루프 " + (j + 1) + " 소요 시간: " + (d / 1_000_000) + " ms\n\n";
            resp.getWriter().write(data);
            resp.getWriter().flush();  // 클라이언트로 즉시 전송
        }

        long totalEndTime = System.nanoTime(); // 전체 실행 종료 시간 측정
        long totalTime = totalEndTime - totalStartTime;

        // 전체 실행 시간을 클라이언트로 전송
        String totalTimeData = "data: 전체 실행 시간: " + (totalTime / 1_000_000) + " ms\n\n";
        resp.getWriter().write(totalTimeData);
        resp.getWriter().flush();
    }

    private static void initialize(Color id) {
        for (int x = 0; x < colors.length; x++) {
            Color[][] plane = colors[x];
            for (int y = 0; y < plane.length; y++) {
                Color[] row = plane[y];
                for (int z = 0; z < row.length; z++) {
                    Color color = new Color(x, y, z);
                    color.add(id);
                    if ((color.r + color.g + color.b) % 42 == 0) {
                        row[z] = color;
                    }
                }
            }
        }
    }
}
