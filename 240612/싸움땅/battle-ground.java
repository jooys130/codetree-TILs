import java.util.*;
import java.io.*;
public class Main {
    // 9시 24분 ~ 12시 50분
    /*
        빈칸이 없는 경우 사람이 어떻게 가는지
        플레이어의 위치
    */
    // 상 우 하 좌 (오른쪽 90도로 회전, 방향 바꾸기)
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int[] ans;
    static int n, m, k;
    // (?) guns 정보 관리
    static List<Integer>[][] guns;
    // player 정보 관리
    static Player[] players; // (1) number
    static int[][] map;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        // 총 정보 저장
        guns = new ArrayList[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                guns[i][j] = new ArrayList<>();
            }
        }
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                guns[i][j].add(Integer.parseInt(st.nextToken()));
            }
        }
        // 플레이어 정보 저장
        players = new Player[m+1];
        map = new int[n][n];
        for (int i = 1; i < m+1; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken())-1;
            int y = Integer.parseInt(st.nextToken())-1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            players[i] = new Player(x, y, d, s, 0);
            map[x][y] = i;
        }
        ans = new int[m+1];
        for (int round = 0; round < k; round++) {
            // 1 ~ n번 플레이어 순서대로
            for (int i = 1; i < m+1; i++) {
                move(i);
                if (!existPlayer(i)) {
                    getGuns(i);
                } else {
                    fight(i);
                }
            }
        }
        for (int i = 1; i < m+1; i++) {
            System.out.print(ans[i] + " ");
        }
        System.out.println();
    }
    public static void fight(int i) {
        Player p1 = players[i];
        int j = map[p1.x][p1.y];
        Player p2 = players[map[p1.x][p1.y]];
        int s1 = p1.cap + p1.gun;
        int s2 = p2.cap + p2.gun;
        // 초기 능력치 + 총의 공격력 비교하여 싸움
        // 값이 같으면 높은 초기 능력치를 가진 사람이 이김
        int win = 0; int L = 0;
        if (s1 == s2) {
            if (p1.cap > p2.cap) {
                win = i;
                L = j;
            }
            else {
                win = j;
                L = i;
            }
        } else if (s1 > s2) {
            win = i;
            L = j;
        } else {
            win = j;
            L = i;
        }
        // 이긴 플레이어는 각 플레이어의 (초기 능력치 + 총의 공격력)의 차이만큼 포인트 획득
        ans[win] += Math.abs(s1-s2);
        // System.out.println("winner " + win + " " + players[win]);
        // System.out.println("loser " + L + " " + players[L]);

        // 진 플레이어는 총을 해당 격자에 내려두고 원래 가진 방향으로 한 칸 이동
        Player loser = players[L];
        guns[loser.x][loser.y].add(loser.gun);
        players[L].gun = 0;
        move2(L);

        // 이긴 플레이어는 승리한 칸에 있는 총 중 가장 높은 공격력을 가진 총 획득하고
            // 나머지는 그대로 격자에 두기
        getGuns(win);
    }
    public static void move2(int num) {
        Player p = players[num];
        // 본인이 향하고 있는 방향대로 한 칸 이동
            // 다른 플레이어가 있거나 격자 범위 밖이면
            // 오른쪽으로 90도 회전하여 빈칸 보이는 순간 이동
        for (int i = 0; i < 4; i++) {
            int nd = (p.dir + i) % 4;
            int nx = p.x + dx[nd];
            int ny = p.y + dy[nd];
            if (nx < 0 || nx >= n || ny < 0 || ny >= n || map[nx][ny] != 0) continue;
            //  (2) 사람 확인
            else {
                p.dir = nd;
                // map[p.x][p.y] = 0; // 원래 잇던 곳
                p.x = nx; p.y = ny;
                map[p.x][p.y] = num; // 일하는 곳
                break;
            }
        }
        // player 정보 업데이트
        // map[p.x][p.y] = 0;
        // p.x = nx; p.y = ny;
        // map[p.x][p.y] = num;

        // 이동 후 총 있으면  (2-1) 동작
        getGuns(num);
    }
    public static void move(int num) {
        Player p = players[num];
        // 본인이 향하고 있는 방향대로 한 칸 이동
        int nx = p.x + dx[p.dir];
        int ny = p.y + dy[p.dir];
        // 격자를 벗어나는 경우 정반대로 방향 바꾸어 이동
        if (nx < 0 || nx >= n || ny < 0 || ny >= n) {
            int nd = (p.dir + 2) % 4;
            nx = p.x + dx[nd];
            ny = p.y + dy[nd];
            p.dir = nd;
        }
        // player 정보 업데이트
        map[p.x][p.y] = 0;
        p.x = nx; p.y = ny;
        // map[p.x][p.y] = num; // 이건 있는지 확인하고 난 후
    }
    public static void getGuns(int k) {
        // 총 있다면 총 획득
        Player p = players[k];
        if (guns[p.x][p.y].size() != 0) {
            Collections.reverse(guns[p.x][p.y]);
            // System.out.println(guns[p.x][p.y]);
            if (p.gun == 0) {
                p.gun = guns[p.x][p.y].get(0);
                guns[p.x][p.y].remove(0);
            } else {
                // 더 공격력이 센 총을 획득하고 원래 가지고 있던 건 그 자리에 둠
                if (p.gun < guns[p.x][p.y].get(0)) {
                    int tmp = guns[p.x][p.y].get(0);
                    guns[p.x][p.y].remove(0);
                    guns[p.x][p.y].add(p.gun);
                    p.gun = tmp;
                }
            }
        }
        // 플레이어 정보 업데이트
        map[p.x][p.y] = k; // (3) 위치 저장
        // System.out.println(map[p.x][p.y]);
    }
    public static boolean existPlayer(int k) {
        // 해당 칸에 플레이어가 있는지 확인
        Player p = players[k];
        return map[p.x][p.y] != 0;
    }
    static class Player {
        int x, y, dir;
        int cap;
        int gun;
        Player(int x, int y, int dir, int cap, int gun) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.cap = cap;
            this.gun = gun;
        }
        @Override
        public String toString() {
            return "( " + x + " " + y + " " + dir + ") " + cap + " " + gun;
        }
    }
    public static void print() {
        for (int i = 0; i < n; i++) {
            System.out.println(Arrays.toString(map[i]));
        }
        System.out.println();
    }
}