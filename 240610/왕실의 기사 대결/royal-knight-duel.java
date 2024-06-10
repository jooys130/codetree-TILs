import java.util.*;
import java.io.*;
public class Main {
    // 상 우 하 좌
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int[][] map; // 체스판
    static int[][] loc;// 기사 위치 기반 number 할당 : 밀리는지 여부 판단하기 위함
    static K[] knights;
    static int[] damages;
    static class K {
        int x, y;
        int h, w;
        int cnt; // 대미지
        K(int x, int y, int h, int w, int cnt) {
            this.x = x;
            this.y = y;
            this.h = h; // 세로
            this.w = w; // 가로
            this.cnt = cnt;
        }
        @Override
        public String toString() {
            return x + " " + y + " " + h + " " + w + " " + cnt;
        }
    }
    static int L, N, Q;
    static Stack<Integer> moved; // LIFO!!
    static Queue<Integer> q;
    static void go(int idx, int dir) {
        if (knights[idx].cnt > 0) {
            bfs(idx, dir);
            update(idx, dir);
        } else {
            return;
        }
    }
    static void calc() {
        int ans = 0;
        for (int i = 1; i < N+1; i++) {
            if(knights[i].cnt > 0) {
                ans += damages[i];
            }
        }
        System.out.println(ans);
    }
    static void update(int order, int dir) {
        while(!moved.isEmpty()) {
            Integer idx = moved.pop();
            K cur = knights[idx];
            // 옮기기 전 위치 없애기
            remove(cur);
            // knights update
            cur.x += dx[dir];
            cur.y += dy[dir];
            // 옮긴 거 갱신 + 대미지 입히기
            int damage = 0;
            for (int i = cur.x; i < cur.x + cur.h; i++) {
                for (int j = cur.y; j < cur.y + cur.w; j++) {
                    loc[i][j] = idx;
                    // 명령받은 기사는 대미지 안 받음
                    if (order != idx && map[i][j] == 1) damage++;
                }
            }
            if (cur.cnt > damage) {
                cur.cnt -= damage;
                damages[idx] += damage;
            } else {
                cur.cnt = 0;
                remove(cur);
            }
        }
        // for (int i = 0; i < L; i++) {
        //     System.out.println(Arrays.toString(loc[i]));
        // }
    }
    static void remove(K cur) {
        // loc에서 기사 넘버 없애기
        for (int i = cur.x; i < cur.x + cur.h; i++) {
            for (int j = cur.y; j < cur.y + cur.w; j++) {
                loc[i][j] = 0;
            }
        }
    }
    static void bfs(int idx, int dir) {
        moved = new Stack<>();
        q = new ArrayDeque<>();
        q.add(idx);
        while (!q.isEmpty()) {
            Integer kk = q.poll();
            // System.out.println(kk);
            moved.add(kk); // 움직이는 대상이 되는 기사 저장
            if (!canMove(kk, dir)) {
                moved = new Stack<>();
                return;
            }
        }
    }
    static boolean canMove(Integer idx, int dir) {
        K cur = knights[idx];
        for (int i = cur.x; i < cur.x + cur.h; i++) {
            for (int j = cur.y; j < cur.y + cur.w; j++) {
                int nx = i + dx[dir];
                int ny = j + dy[dir];
                // System.out.println("next " + nx + " " + ny);
                if (nx < 0 || nx >= L || ny < 0 || ny >= L || map[nx][ny] == 2) return false;
                if (loc[nx][ny] == idx) continue; // 원래 기사의 영역인 경우
                if (loc[nx][ny] != 0) {
                    if (!q.contains(loc[nx][ny])) q.add(loc[nx][ny]);
                }
            }
        }
        return true;
    }
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken()); // 체스판 크기
        N = Integer.parseInt(st.nextToken()); // 기사 수
        Q = Integer.parseInt(st.nextToken()); // 명령 수
        map = new int[L][L];
        loc = new int[L][L];
        for (int i = 0; i < L; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < L; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        knights = new K[N+1];
        for (int i = 1; i < N+1; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken()); // 초기 체력
            knights[i] = new K(r, c, h, w, k);
            for (int x = r; x < r + h; x++) {
                for (int y = c; y < c + w; y++) {
                    loc[x][y] = i;
                }
            }
        }
        damages = new int[N+1];
        // 왕이 특정 기사에게 명령을 내린다
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken()); // 기사 number
            int d = Integer.parseInt(st.nextToken());
            go(idx, d);
        }
        // 대결이 끝나고 생존한 기사들이 받은 대미지의 합
        calc();
    }
}