import java.util.*;
import java.io.*;
public class Main {
    // 우 하 좌 상
    static int[] dx = {0, 1, 0, -1, -1, -1, 1, 1, 0};
    static int[] dy = {1, 0, -1, 0, -1, 1, -1, 1, 0};
    static int N, M, K;
    static int[][] map;
    static int turn;
    static boolean[][] visited;
    static boolean[][] checked; // 공격 관련 정보 저장
    static int[][] order; // 순서 저장
    // 포탑 관리
    static List<Node> attackCandi;
    static Node attacker, attacked; // 공격자, 공격받는 거 저장
    static Pos[][] ways; // 가는 길에 만난 포탑 위치 저장
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N][M];
        order = new int[N][M]; // 숫자가 클수록 빠른 거
        attackCandi = new ArrayList<>();
        ways = new Pos[N][M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // go()
        while (K-- > 0) {
            attackCandi = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (map[i][j] > 0) {
                        // 공격력 0 이하되면 더이상 공격할 수 없음
                        attackCandi.add(new Node(map[i][j], i, j, order[i][j]));
                    }
                }
            }
            // 0이 아닌 포탑 1개 되면 즉시 중지
            if (attackCandi.size() <= 1) break; // 즉시 중지이므로 추가해야할수도
            
            checked = new boolean[N][M];
            visited = new boolean[N][M];
            turn++;

            // 1. 가장 약한 포탑을 공격자로 선정하여 N+M만큼 공격력 증가
            // 2. 공격자가 가장 강한 포탑 찾아서 공격
            Collections.sort(attackCandi);
            attacker = attackCandi.get(0);
            attacked = attackCandi.get(attackCandi.size()-1);
            map[attacker.x][attacker.y] += (N+M);
            attacker.value += (N+M);
            order[attacker.x][attacker.y] = turn; // 순서 저장
            attacker.order = turn;
            checked[attacker.x][attacker.y] = true;

            // System.out.println("공격자" + attacker);
            // System.out.println("받는자" + attacked);
            
            boolean reachable = laser();
            if (!reachable) {
                potan();
            }

            // 3. 공격력이 0 이하면 부서짐 => attckCandi 갱신 (위에서)
            // 4. 포탑 정비 => 공격과 무관한 곳은 공격력 1씩 증가
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (!checked[i][j] && map[i][j] != 0) map[i][j]++;
                }
            }
            // for (int i= 0; i < N; i++) {
            //     System.out.println(Arrays.toString(map[i]));
            // }
        }
        // 5. 답
        int ans = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                ans = Math.max(ans, map[i][j]);
            }
        }
        System.out.println(ans);
    }
    public static boolean laser() {
        // (1)  레이저 공격 시도
        boolean reachable = false;
        Queue<Pos> q = new ArrayDeque<>();
        q.add(new Pos(attacker.x, attacker.y));
        visited[attacker.x][attacker.y] = true;
        checked[attacker.x][attacker.y] = true;

        while(!q.isEmpty()) {
            Pos cur = q.poll();
            if (cur.x == attacked.x && cur.y == attacked.y) {
                reachable = true;
                break;
            }
            for (int i = 0; i < 4; i++) {
                // 가장자리는 이어짐  (2, 4) -> (2, 1)
                int nx = (cur.x + dx[i] + N) % N;
                int ny = (cur.y + dy[i] + M) % M;
                // 값이 0인 곳 지나갈 수 없음
                if (map[nx][ny] == 0 || visited[nx][ny]) continue;
                q.add(new Pos(nx, ny));
                visited[nx][ny] = true;
                ways[nx][ny] = new Pos(cur.x, cur.y); // 이 전 위치 저장
            }   
        }
        // System.out.println(reachable + "  " + attacker.value);
        // 공격할 수 있다면
        if(reachable) {
            map[attacked.x][attacked.y] -= attacker.value;
            checked[attacked.x][attacked.y] = true;
            if (map[attacked.x][attacked.y] < 0) map[attacked.x][attacked.y] = 0;
            // 역추적 **
            Pos prev = ways[attacked.x][attacked.y];
            int px = prev.x, py = prev.y;
            while(true) {
                if (px == attacker.x && py == attacker.y) break;
                map[px][py] -= attacker.value/2;
                if (map[px][py] < 0) map[px][py] = 0;
                checked[px][py] = true;
                int ppx = ways[px][py].x;
                int ppy = ways[px][py].y;
                px = ppx; py = ppy;
            }
        }
        // for (int i = 0; i < N; i++) {
        //     System.out.println(Arrays.toString(map[i]));
        // }
        return reachable;
    }
    public static void potan() {
        // (2) 최단 경로가 존재하지 않으면 포탄 공격
        map[attacked.x][attacked.y] -= attacker.value;
        if (map[attacked.x][attacked.y] < 0) map[attacked.x][attacked.y] = 0;
        // attacked.value -= attacker.value;
        checked[attacked.x][attacked.y] = true;
        for (int i = 0; i < 9; i++) {
            // 추가 피해가 반대편 격자까지
            int nx = (attacked.x + dx[i] + N) % N;
            int ny = (attacked.y + dy[i] + M) % M;
            if (nx == attacker.x && ny == attacker.y) continue;
            if (nx == attacked.x && ny == attacked.y) {
                map[nx][ny] -= attacker.value;
                if (map[nx][ny] < 0) map[nx][ny] = 0;
                checked[nx][ny] = true;
            } else {
                map[nx][ny] -= attacker.value / 2;
                if (map[nx][ny] < 0) map[nx][ny] = 0;
                checked[nx][ny] = true;
            }
        }
    }
    static class Node implements Comparable<Node>{
        int x, y, value, order;
        Node(int value, int x, int y, int order) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.order = order;
        }
        // 가장 최근에 공격한 포탑 > 행과 열의 합이 큰 > 큰 열
        @Override 
        public int compareTo(Node o) {
            if (this.value == o.value) {
                if (this.order == o.order) {
                    if (this.x + this.y == o.x + o.y) {
                        return o.y - this.y;
                    }
                    return (o.x + o.y) - (this.x + this.y); 
                }
                return o.order - this.order;
            }
            return this.value - o.value;
        }
        @Override
        public String toString() {
            return value + " :" + x + " " + y;
        }
    }
    static class Pos {
        int x, y;
        Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "(" + x + " "+ y ;
        }
    }
}