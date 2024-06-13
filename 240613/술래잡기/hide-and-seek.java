import java.util.*;
import java.io.*;
public class Main {
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int ans;
    static int N, M, H, K; // n은 홀수
    static boolean[][] trees;
    static Node[] people;
    static int X, Y, D; // 술래의 위치
    static boolean reverse; 
    public static void main(String[] args) throws IOException {
        // 9시 28분
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        trees = new boolean[N][N];
        people = new Node[M];
        X = N / 2; Y = N / 2;
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int tmp = Integer.parseInt(st.nextToken());
            // 도망자 종류 두 가지
                // 좌우로 움직이는 사람 오른쪽 보고 시작
                // 상하로 움직이는 사람 아래 보고 시작
            int d = tmp == 1 ? 1 : 2;
            people[i] = new Node(x, y, d, true);
        }
        for (int i = 0; i < H; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            trees[x][y] = true;
        }
        // 나무랑 도망자 중복 위치 가능
        // System.out.println(Arrays.toString(people));
        // System.out.println("초기 술래 " + X + " " + Y + " " + D + " " + reverse);

        // for (int q = 0; q < N * N * 2 ; q++) {
        //     moveIt();
        //     System.out.println("다음 술래 " + X + " " + Y + " " + D + " " + reverse);
        // }
        
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < M; j++) {
                // 술래와 거리가 3 이하이고 살아있는 도망자에 대해서
                if (people[j].alive && getDist(X, people[j].x, Y, people[j].y) <= 3) {
                    moveRunner(j);
                }
            }
            // System.out.println(Arrays.toString(people));
            moveIt();
            // System.out.println("다음 술래 " + X + " " + Y + " " + D + " " + reverse);
            go(i+1);
        }
        System.out.println(ans);
    }
    public static void moveRunner(int num) {
        // 바라보고 있는 방향으로 1칸
        Node p = people[num];
        int nx = p.x + dx[p.d];
        int ny = p.y + dy[p.d];
        // System.out.println(nx + " " + ny);
        if (nx < 0 || nx >= N || ny < 0 || ny >= N) {
            p.d = (p.d + 2) % 4; // 방향 바꾸기
            nx = p.x + dx[p.d];
            ny = p.y + dy[p.d];
            // System.out.println("왜?" + num + "!! " + nx + " " + ny);
            // 다시 타입 체크 ??
            if (X == nx && Y == ny) return;
            p.x = nx; p.y = ny; // 술래 없을 때 이동
        } else {
            if (X == nx && Y == ny) return;
            // 술래 없을 때 이동 (나무 있어도)
            p.x = nx; p.y = ny;
        }
    }
    public static void moveIt() {
        // 시간을 기준으로 위치가 바뀜 -> 돌리는 게 아니라 값 할당 ?
        // 움직이고 해당 위치에 대해 방향 할당 ??
        X += dx[D];
        Y += dy[D];
        if (X == N / 2 && Y == N / 2) {
            D = (D + 2) % 4;
            if (reverse) reverse = !reverse;
        } else if (X == N/2-1 && Y == N /2 ) {
            if (reverse) {
                D = (D - 1 + 4) % 4;
            } else {
                D = (D + 1) % 4;
            }
        } else if (X == 0 && Y == 0) {
            D = (D + 2) % 4;  // 반대 방향
            reverse = true;
        } else if (X >= N / 2 || Y >= N / 2) {
            if (X == Y || X + Y == N -1) {
                // 2, 3, 4사분면에 대해서
                if (reverse) {
                    D = (D - 1 + 4) % 4;
                } else {
                    D = (D + 1) % 4;
                }
            }
        } else if (X < N /2 && Y < N / 2) {
            // 1사분면에서 바뀌는 부분
            if (X + 1 == Y) {
                if (reverse) {
                    D = (D - 1 + 4) % 4;
                } else {
                    D = (D + 1) % 4;
                }
            }
        }
        // 이외의 경우는 방향 유지

        // 위 방향으로 시작해서 달팽이 모양으로 움직임(상 우 하 좌)
        // 이동 후 방향 틀어지는 지점이면 방향을 바로 틀어줌
        // (0, 0) 혹은 정 중앙일 때도 etc
    }
    public static void go(int turn) {
        int runnerCnt = 0; // 잡힌 도망자 개수
        // 술래가 바라보고 있는 방향으로 3칸
        List<int[]> checkPoint = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int nx = X + dx[D] * i;
            int ny = Y + dy[D] * i;
            // 나무가 있는 칸은 안 보임
            if (nx < 0 || nx >= N || ny < 0 || ny >= N || trees[nx][ny]) continue;
            checkPoint.add(new int[] {nx, ny});
        }
        // 술래 개수 세고 잡기
        for (int i = 0; i < checkPoint.size(); i++) {
            int[] pos = checkPoint.get(i);
            // System.out.println("checkPoint" + Arrays.toString(pos));
            for (int num = 0; num < M; num++) {
                if (people[num].alive && people[num].x == pos[0] && people[num].y == pos[1]) {
                    // System.out.println(Arrays.toString(pos) + " <- " + people[num]);
                    runnerCnt++;
                    people[num].alive = false;
                }
            }
        }
        ans += turn * runnerCnt;
    }
    public static int getDist(int x1, int x2, int y1, int y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }
    static class Node {
        int x, y, d;
        boolean alive;
        Node (int x, int y, int d, boolean alive) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.alive = alive;
        }
        @Override
        public String toString() {
            return x + " " + y + " " + d;
        }
    }
}