import java.util.*;
import java.io.*;
public class Main {
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, -1, 0, 1};
    static int[] ddx = {-1, -1, 1, 1};
    static int[] ddy = {-1, 1, -1, 1};
    static class Pos implements Comparable<Pos>{
        int x; int y;
        int count;
        Pos(int x, int y, int count) {
            this.x = x;
            this.y = y;
            this.count = count;
        }
        @Override
        public int compareTo(Pos o) {
            if (this.count == o.count) {
                if (this.x == o.x) {
                    return this.y - o.y;
                }
                return this.x - o.x;
            }
            return o.count - this.count;
        }
        @Override
        public String toString() {
            return x + " " + y + " " + count;
        }
    }
    static int n, m, k, c;
    static int[][] trees;
    static boolean[][] visited; // 제초제 기록
    static List<Pos> candidates;
    static int ans;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        trees = new int[n][n];
        visited = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                trees[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        int year = 0;
        while (m-->0) {
            candidates = new ArrayList<Pos>();
            grow();
            breeding();
            spray();
            if (year++ == c) {
                visited = new boolean[n][n];
            }
        }
        System.out.println(ans);
    }
    private static void grow() {
        // 4방향 기준 개수 구하기
        int[][] tmp = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (trees[i][j] == 0 || trees[i][j] == -1) continue;
                int count = 0;
                for (int d = 0; d < 4; d++) {
                    int nx = i + dx[d];
                    int ny = j + dy[d];
                    if (outOfRange(nx, ny)) continue;
                    if (trees[nx][ny] != 0) count++;
                }
                tmp[i][j] = count;
            }
        }
        // 동시에 성장
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                trees[i][j] += tmp[i][j];
            }
        }
        command("grow");
        print(trees);
    }

    private static void breeding() {
        // 벽, 다른 나무, 제초제가 없어야 함
        // 번식 가능한 곳 개수만큼 나눈다
        int[][] tmp = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (trees[i][j] == 0 || trees[i][j] == -1) continue;
                int count = 0; // 번식이 가능한 칸의 개수
                List<int[]> pos = new ArrayList<>();
                for (int d = 0; d < 4; d++) {
                    int nx = i + dx[d];
                    int ny = j + dy[d];
                    if (outOfRange(nx, ny) || trees[nx][ny] != 0) continue;
                    count++;
                    pos.add(new int[] {nx, ny});
                }
                for (int[] p : pos) {
                    tmp[p[0]][p[1]] += trees[i][j]/count;
                }
            }
        }
        // 동시에 번식
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                trees[i][j] += tmp[i][j];
            }
        }
        command("breeding");
        print(trees);
    }

    private static void spray() {
        // 가장 많이 박멸되는 칸에 제초제 뿌림
            // 대각선 방향으로 k칸 만큼 + 본인 거
            // bfs로 박멸되는 나무 개수 구하기
                // 벽, 나무 아예 없는 경우 거기까지만
                // c년 만큼 작용
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (trees[i][j] == 0 || trees[i][j] == -1) continue;
                int count = trees[i][j];
                for (int d = 0; d < 4; d++) {
                    for (int size = 1; size <= k; size++) {
                        int nx = i + ddx[d] * size;
                        int ny = j + ddy[d] * size;
                        if (outOfRange(nx, ny) || trees[nx][ny] == 0) break;
                        count += trees[nx][ny];
                    }
                }
                candidates.add(new Pos(i, j, count));
            }
        }
        Collections.sort(candidates);
        // 제초제 뿌리기
        Pos target = candidates.get(0);
        ans += target.count;
        visited[target.x][target.y] = true;
        for (int d = 0; d < 4; d++) {
            for (int size = 1; size <= k; size++) {
                int nx = target.x + ddx[d] * size;
                int ny = target.y + ddy[d] * size;
                if (outOfRange(nx, ny)) break;
                visited[nx][ny] = true;
                trees[nx][ny] = 0;
            }
        }
        command("spary");
        for (int i = 0; i < n; i++) {
            System.out.println(Arrays.toString(visited[i]));
        }
    }

    private static boolean outOfRange(int x, int y) {
        return x < 0 || x >= n || y < 0 || y >= n || trees[x][y] == -1 || visited[x][y];
    }

    public static void print(int[][] arr) {
        for (int i = 0; i < n; i++) {
            System.out.println(Arrays.toString(arr[i]));
        }
    }

    public static void command(String msg) {
        System.out.println(msg);
    }
}