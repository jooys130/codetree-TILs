import java.util.*;
import java.io.*;
public class Main {
    static int N;
    static int ans;
    static int[][] map;
    static int[][] tmp;
    static int[][] group; // 1 -> 2의 개수 저장
    static int[] groupCnt;
    static boolean[][] visited;
    static int[] dx = {0, -1, 0, 1};
    static int[] dy = {-1, 0, 1, 0};
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        map = new int[N][N];
        tmp = new int[N][N];
        for(int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // 초기 - 3회전 이후 예술 점수의 총합
        for (int turn = 0; turn < 4; turn++) {
            floodfill();
            ans += getScore();
            rotate();
        }
        System.out.println(ans);
    }
    public static void floodfill() {
        // getCnt + getCombi
        visited = new boolean[N][N];
        group = new int[N][N]; 
        groupCnt = new int[N * N + 1];
        // combi = new ArrayList<>();
        // G = new ArrayList<>(); // [?] 중복된 숫자 있으니까 배열 아닌 List로 받기
        int num = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j])
                    bfs(i, j, ++num);
            }
        }
        // for (int i = 0; i < N; i++) {
        //     System.out.println(Arrays.toString(group[i]));
        // }
        // System.out.println(Arrays.toString(groupCnt));
    }
    public static void bfs(int x, int y, int num) {
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] {x, y});
        visited[x][y] = true;
        group[x][y] = num;
        groupCnt[num] = 1;
        while(!q.isEmpty()) {
            int[] pos = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = pos[0] + dx[i];
                int ny = pos[1] + dy[i];
                if (nx < 0 || nx >= N || ny < 0 || ny >= N || visited[nx][ny]) continue;
                if (map[nx][ny] == map[x][y]) {
                    q.add(new int[] {nx, ny});
                    visited[nx][ny] = true;
                    group[nx][ny] = num; // combi
                    groupCnt[num]++;
                }
            }   
        }
    }
    public static int getScore() {
        int score = 0;
        // 변의 개수를 여기서 구하는 게 포인트
            // 숫자 다를 때 즉 맞닿아 있을 때마다 점수 더해주기 -> 양쪽으로 두번이니까 /2 해서 더한다
        // 원래는 탐색하면서 개수 세기 -> 중복 숫자로 올바른 업데이트 안 됨
        // 이후, 넘버링해서 combi 구하려고 했다 그럼 bfs 두번 필요
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int d = 0; d < 4; d++) {
                    int nx = i + dx[d];
                    int ny = j + dy[d];
                    if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
                    if (map[i][j] != map[nx][ny]) {
                        // 다른 값일 때 재넘버링된 숫자 가져오기
                        int g1 = group[i][j]; int g2 = group[nx][ny];
                        int aValue = map[i][j]; int bValue = map[nx][ny];
                        int aCnt = groupCnt[g1]; int bCnt = groupCnt[g2];
                        score += (aCnt + bCnt) * aValue * bValue;
                    }
                }
            }
        }
        // System.out.println(score/2);
        return score/2;
    }
    public static void rotate() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tmp[i][j] = map[i][j];
            }
        }
        rotateCenter();
        rotate(0, 0, N/2);
        rotate(N/2+1, 0, N/2);
        rotate(0, N/2+1, N/2);
        rotate(N/2+1, N/2+1, N/2);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                map[i][j] = tmp[i][j];
            }
        }
        // System.out.println("?");
        // for (int i = 0; i < N; i++) {
        //     System.out.println(Arrays.toString(map[i]));
        // }
    }
    public static void rotateCenter() {
        // 통째로 십자가 모양만 반시계 방향으로 90도
        /*
            (N/2, 0) <- (0, N/2) 세로
            (N-1, N/2) <- (N/2, 0)
            (N/2, N-1) <- (N-1, N/2) 세로
        */
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // 세로 줄에 대해서
                if (j == N/2) tmp[j][i] = map[i][j];
                // 가로 줄에 대해서
                else if (i == N/2) tmp[N-j-1][i] = map[i][j];
            }
        }
    }
    public static void rotate(int sx, int sy, int D) {
        // 십자 모양을 제외한 4개의 정사각형 시계 방향으로 90도
        for (int i = 0; i < D; i++) {
            for (int j = 0; j < D; j++) {
                tmp[sx + j][sy + D - 1 -  i] = map[sx + i][sy + j];
            }
        }
    }
    public static int harmony (int cntA, int cntB, int a, int b, int cntL) {
        return (cntA + cntB) * a * b * cntL;
    }
}