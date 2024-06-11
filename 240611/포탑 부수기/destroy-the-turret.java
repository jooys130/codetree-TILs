import java.util.*;
import java.io.*;
public class Main {
    // 우 하 좌 상
    static int[] dx = {0, 1, 0, -1, -1, -1, 1, 1};
    static int[] dy = {1, 0, -1, 0, -1, 1, -1, 1};
    static int N, M, K;
    static int[][] map;
    static boolean[][] visited;
    static boolean[][] checked; // 공격 관련 정보 저장
    static int[][] order; // 순서 저장
    static List<Node> attackCandi;
    static int totalCnt;
    static Node attacker, attacked; // 공격자, 공격받는 거 저장
    static Stack<Pos> ways; // 가는 길에 만난 포탑 위치 저장
    static boolean reachable;
    static Queue<Pos> targets; // 무조건 맨 앞에가 타겟이 되도록 구성
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N][M];
        order = new int[N][M]; // 숫자가 클수록 빠른 거
        attackCandi = new ArrayList<>();
        ways = new Stack<>();
        targets = new ArrayDeque<>();
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if (map[i][j] != 0) {
                    // 공격력 0이하되면 더이상 공격할 수 없음
                    attackCandi.add(new Node(map[i][j], i, j, i+j, order[i][j]));
                    totalCnt++;
                }
            }
        }
        while (K-- > 0) {
            // 0이 아닌 포탑 1개 되면 즉시 중지
            if (totalCnt == 0) break; // 즉시 중지이므로 추가해야할수도
            checked = new boolean[N][M];
            // 1. 가장 약한 포탑을 공격자로 선정하여 N+M만큼 공격력 증가
            Collections.sort(attackCandi);
            // System.out.println("+======");
            // System.out.println(attackCandi);
            attacker = attackCandi.get(0);
            // 2. 공격자가 가장 강한 포탑 찾아서 공격
            attacked = attackCandi.get(attackCandi.size()-1);
            // 1-2. attakcer advantage
            map[attacker.x][attacker.y] += (N+M);
            attacker.value += (N+M);
            // 1-3. update status
            checked[attacker.x][attacker.y] = true;
            order[attacker.x][attacker.y] = K;
            // System.out.println("공격자" + attacker);
            // System.out.println("받는자" + attacked);
            checkReachable();
            if (reachable) {
                laser();
            } else {
                potan();
            }
            // 3. 공격력이 0 이하면 부서짐 => attckCandi 갱신
            attackCandi.clear();
            // 4. 포탑 정비
                //  공격과 무관한 곳은 공격력 1씩 증가
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (!checked[i][j] && map[i][j] != 0) map[i][j]++;
                }
            }
            for (int i = 0; i < N; i++) {
                for (int j = 0; j <M; j++) {
                    if (map[i][j] > 0) {
                        attackCandi.add(new Node(map[i][j], i, j, i+j, order[i][j]));
                    } else {
                        map[i][j] = 0;
                    }
                }
            }
            // for (int i= 0; i < N; i++) {
            //     System.out.println(Arrays.toString(map[i]));
            // }
        }
        // 5. 답 !
        int ans = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                ans = Math.max(ans, map[i][j]);
            }
        }
        System.out.println(ans);
    }
    public static void checkReachable() {
        // 공격자 위치에서 공격 대상 포탑까지 "최단 경로"인 곳에 대해서
        // (**) 경로 기억해야하니까 dfs?? 근데 또 최단 경로여야 하는데..?
        reachable = false;
        visited = new boolean[N][M];
        bfs();
        // System.out.println(reachable);
    }
    public static void bfs() {
        Queue<Pos> q = new ArrayDeque<>();
        q.add(new Pos(attacker.x, attacker.y, -1));
        visited[attacker.x][attacker.y] = true;
        while(!q.isEmpty()) {
            Pos cur = q.poll();
            ways.add(cur);
            if (cur.x == attacked.x && cur.y == attacked.y) {
                reachable = true;
                break;
            }
            for (int i = 0; i < 4; i++) {
                // 가장자리는 이어짐  (2, 4) -> (2, 1)
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];
                if (nx < 0 || nx >= N || ny < 0 || ny >= M) {
                    nx = (nx + N) % N;
                    ny = (ny + M) % M;
                }
                // 값이 0인 곳 지나갈 수 없음
                if (map[nx][ny] == 0 || visited[nx][ny]) continue;
                q.add(new Pos(nx, ny, i));
                visited[nx][ny] = true;
            }   
        }
    }
    public static void laser() {
        // (1)  레이저 공격 시도
        // System.out.println(ways);
        Pos cur = ways.pop();
        // System.out.println("start" + cur);
        map[cur.x][cur.y] -= attacker.value;
        checked[cur.x][cur.y] = true;
        // attacked.value -= attacker.value;
        int d = (cur.dir+2)%4;
        int nx = cur.x + dx[d];
        int ny = cur.y + dy[d];
        while(!ways.isEmpty()) {
            Pos next = ways.pop();
            // System.out.println(next);
            if (next.dir == -1) break;
            if (next.x == nx && next.y == ny) {
                map[nx][ny] -= attacker.value / 2;
                checked[nx][ny] = true;
                d = (next.dir+2) % 4;
                nx += dx[d]; ny += dy[d];
            }
        }
    }
    public static void potan() {
        // (2) 최단 경로가 존재하지 않으면 포탄 공격
        map[attacked.x][attacked.y] -= attacker.value;
        // attacked.value -= attacker.value;
        checked[attacked.x][attacked.y] = true;
        for (int i = 0; i < 8; i++) {
            // 추가 피해가 반대편 격자까지
            int nx = (attacked.x + dx[i] + N) % N;
            int ny = (attacked.y + dy[i] + M) % M;
            if (map[nx][ny] == 0) continue;
            map[nx][ny] -= attacker.value / 2;
            checked[nx][ny] = true;
        }
    }
    static class Node implements Comparable<Node>{
        int x, y, sum, order, value;
        Node(int value, int x, int y, int sum, int order) {
            this.x = x;
            this.y = y;
            this.sum = sum;
            this.order = order;
            this.value = value;
        }
        // 가장 최근에 공격한 포탑 > 행과 열의 합이 큰 > 큰 열
        @Override 
        public int compareTo(Node o) {
            if (this.value == o.value) {
                if (this.order == o.order) {
                    if (this.sum == o.sum) {
                        return o.y - this.y;
                    }
                    return o.sum - this.sum;
                }
                return o.order - this.order;
            }
            return this.value - o.value;
        }
        @Override
        public String toString() {
            return value + " :" + x + " " + y + " (" + sum + ") " + order;
        }
    }
    static class Pos {
        int x, y, dir;
        Pos(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
        @Override
        public String toString() {
            return "(" + x + " "+ y + " "  + dir + ")";
        }
    }
    /*
    public static void dfs(int x, int y) {
        ways.add(new Pos(x, y)); // 최단 경로 저장
        visited[x][y] = true;
        if (x == attacked.x && y == attacked.y) {
            reachable = true;
            return;
        }
        for (int i = 0; i < 4; i++) {
            // 가장자리는 이어짐  (2, 4) -> (2, 1)
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx < 0 || nx >= N || ny < 0 || ny >= M) {
                continue;
                // nx = (nx + N) % N;
                // ny = (ny + M) % M;
            }
            // 값이 0인 곳 지나갈 수 없음
            if (map[nx][ny] != 0 && !visited[nx][ny]) {
                dfs(nx, ny);
            }
        }
    }
    */
}