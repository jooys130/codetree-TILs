import java.util.*;
import java.io.*;
public class Main {
    static int R, C, K;
    static int[][] info;
    // 북 동 남 서
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int[][] forest;
    static int ans;
    static int[] center = {-1, -1};
    static int col, dir;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        forest = new int[R][C];
        info = new int[K][2];
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            info[i][0] = Integer.parseInt(st.nextToken()) - 1;
            info[i][1] = Integer.parseInt(st.nextToken());
        }
        for (int i = 0; i < K; i++) {
            moveGolem(i);
        }
        System.out.println(ans);
    }
    public static void moveGhost() {
        // System.out.println(center[0] + " " + center[1] + " " + dir);
        int tmp = bfs(center[0], center[1]);
        ans+=(tmp+1);
        // System.out.println("ans--->" +  ans);
    }
    public static int bfs(int x, int y) {
        int maxY = 0;
        boolean[][] visited = new boolean[R][C];
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] {x, y});
        visited[x][y] = true;
        while(!q.isEmpty()) {
            int[] cur = q.poll();
            // System.out.println(Arrays.toString(cur) + " " + forest[cur[0]][cur[1]]);
            maxY = Math.max(maxY, cur[0]);
            for (int i = 0; i < 4; i++) {
                int nx = cur[0] + dx[i];
                int ny = cur[1] + dy[i];
                if (outOfRange(nx, ny) || forest[nx][ny] == 0 || visited[nx][ny]) continue;
                // 다음은 어떤 노드?
                int num = forest[cur[0]][cur[1]];
                if (num < 0 && num * (-1) != forest[nx][ny]) {
                    // 출구인 경우 다른 글렘으로 이동 가능
                    q.offer(new int[] {nx, ny});
                    visited[nx][ny] = true;
                }
                else if (num == forest[nx][ny] || num * (-1) == forest[nx][ny]) {
                    // 같은 글렘 안인 경우
                    q.offer(new int[] {nx, ny});
                    visited[nx][ny] = true;
                }
            }
        }
        return maxY;
    }
    public static void moveGolem(int i) {
        // System.out.println(i);
        col = info[i][0];
        // System.out.println(col);
        dir = info[i][1];
        if (forest[0][col] != 0) {
            return;
        }
        center[0] = -1; center[1] = -1;
        goDown();
        // System.out.println(Arrays.toString(center));
        // 서쪽으로 이동할 수 있으면
        if (center[0] < R-2 && center[1] > 1) {
            goWest();
            // System.out.println(Arrays.toString(center));
        }
        if (center[0] < R-2 && center[1] < C-2) {
            goEast();
            // System.out.println(Arrays.toString(center));
        }
        // 가장 위만 확인
        if (outOfRange(center[0]-1, center[1])) {
            forest = new int[R][C]; // 숲 초기화
            return;
        }
        // 표시
        forest[center[0]][center[1]] = (i+1);
        for (int k = 0; k < 4; k++) {
            int nx = center[0] + dx[k];
            int ny = center[1] + dy[k];
            if (k == dir) {
                forest[nx][ny] = -1 * (i+1);
            } else {
                forest[nx][ny] = (i+1);
            }
        }
        // print();
        moveGhost();
    }
    public static void goDown() {
        for (int i = center[0]; i < R; i++) {
            if (canGo(i+2, col) && canGo(i+1, col-1) && canGo(i+1, col+1)) {
                continue;
            }
            // System.out.println("center" + i + " " + col);
            // 더 이상 . 못갈 . 때
            center[0] = i;
            center[1] = col;
            return;
        }
    }
    public static void goWest() {
        while(center[1] > 0) {
            int row = center[0];
            col = center[1];
            if ((outOfRange(row, col-2) || canGo(row, col-2)) 
            && (outOfRange(row-1, col-1) || canGo(row-1, col-1))
            && (outOfRange(row+1, col-1) || canGo(row+1, col-1))
                && canGo(row+2, col-1) && canGo(row+1, col-2)) {
                // 반시계방향으로 출구 이동
                dir = (dir - 1 + 4) % 4;
                // 위치 이동
                center[0]++;
                center[1]--;
            } else {
                break;
            }
        }
    }
    public static void goEast() {
        while(center[1] < C-1) {
            int row = center[0];
            col = center[1];
            if ((outOfRange(row, col+2) || canGo(row, col+2))
            && (outOfRange(row-1, col+1) || canGo(row-1, col+1))
            && (outOfRange(row+1, col+1) || canGo(row+1, col+1))
                && canGo(row+2, col+1) && canGo(row+1, col+2)) {
                // 시계방향으로 출구 이동
                dir = (dir + 1 + 4) % 4;
                // 위치 이동
                center[0]++;
                center[1]++;
            } else {
                break;
            }
        }
    }
    public static boolean canGo(int x, int y) {
        // if (!outOfRange(x, y)) {
        //     System.out.println(x + " " + y + " " + (forest[x][y] == 0));
        // }
        return 0 <= x && x < R && 0 <= y && y < C && forest[x][y] == 0;
    }
    public static boolean outOfRange(int x, int y) {
        return x < 0 || x >= R || y < 0 || y >= C;
    }
    public static void print() {
        for (int i = 0; i < R; i++) {
            System.out.println(Arrays.toString(forest[i]));
        }
    }
}