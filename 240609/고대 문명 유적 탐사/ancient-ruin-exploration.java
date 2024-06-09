import java.util.*;
import java.io.*;
public class Main {
    static int[] ans;
    static int K, M;
    static int[][] map;
    static Queue<Integer> wall;
    static int[] dx = {0, 1, -1, 0};
    static int[] dy = {-1, 0, 0, 1};
    static PriorityQueue<Pos> remove; // 제거 순서 기억하기 위함
    static class Node implements Comparable<Node> {
        // 우선순위: 획득할 수 있는 최대 조각 개수 -> 작은 회전 각도 -> 작은 열 -> 작은 행
        int x, y;
        int cnt, angle;
        Node(int x, int y, int cnt, int angle) {
            this.x = x;
            this.y = y;
            this.cnt = cnt;
            this.angle = angle;
        }
        @Override
        public int compareTo(Node s) {
            if (this.cnt == s.cnt) {
                if (this.angle == s.angle) {
                    if (this.y == s.y) {
                        return this.x - s.x;
                    }
                    return this.y - s.y;
                }
                return this.angle - s.angle;
            }
            return s.cnt - this.cnt; //내림
        }
        @Override
        public String toString() {
            return "(" + x + ", " + y + ") " + cnt + " " + angle;
        }
    }
    static class Pos implements Comparable<Pos> {
        // 우선순위: 작은 열 -> 큰 행
        int x, y;
        Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public int compareTo(Pos o) {
            if(this.y == o.y) {
                return o.x - this.x;
            }
            return this.y - o.y;
        }
        @Override
        public String toString() {
            return "(" + x + ", " + y + ") ";
        }
    }
    public static void main(String[] args) throws IOException {
        // 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[5][5];
        wall = new ArrayDeque<>();
        ans = new int[K]; // 턴 개수만큼 저장
        for (int i = 0; i < 5; i++) {
        	st = new StringTokenizer(br.readLine());
        	for (int j = 0; j < 5; j++) {
        		map[i][j] = Integer.parseInt(st.nextToken());
        	}
        }
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            wall.add(Integer.parseInt(st.nextToken()));
        }
        // 턴 실행
        for (int t = 0; t < K; t++) {
            getRotation(t);
        }
        for (int k : ans) {
            if (k == 0) break;
            System.out.print(k + " ");
        }
    }
    public static void getRotation(int t) {
        List<Node> candi = new ArrayList<>();
        // 모든 중심 좌표에 대해 회전하여 최대 조각 개수 구하기
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                for (int r = 1; r < 4; r++) {
                    int[][] newMap = rotate(i-1, j-1, r);
                    // print(newMap);
                    int cnt = bfs(newMap); // 돌린 거 bfs로 개수 세기
                    if (cnt > 0) {
                        candi.add(new Node(i, j, cnt, r));
                    }
                }
            }
        
        }
        if (candi.isEmpty()) {
            return;
        }
        Collections.sort(candi);
        // System.out.println("candi-->" + candi);

        // candi.get(0)에 대해 유물 제거
        Node pick = candi.get(0);
        int[][] newMap = rotate(pick.x - 1, pick.y - 1, pick.angle);
        map = newMap; // 맵 수정
        int cnt = bfs(newMap);
        // 벽면 숫자 다시 사용할 수 없음
        int sum = 0; // 획득한 유물의 가치의 총 합

        // 한 턴에서 유물 더이상 획득할 수 없을 때까지
        while (cnt > 0) {
            while(!remove.isEmpty() && !wall.isEmpty()) {
                Pos p = remove.poll();
                map[p.x][p.y] = wall.poll();
            }
            sum += cnt;
            cnt = bfs(map);
        }
        ans[t] = sum;
    }
    public static int bfs(int[][] map) {
        boolean[][] visited = new boolean[5][5];
        Queue<Pos> q = new ArrayDeque();
        remove = new PriorityQueue<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j]) {
                    q.add(new Pos(i, j));
                    visited[i][j] = true;
                    // 같은 숫자인 위치 저장
                    List<Pos> same = new ArrayList<>();
                    same.add(new Pos(i, j));
                    // 주위로 탐색
                    while(!q.isEmpty()) {
                        Pos cur = q.poll();
                        for (int d = 0; d < 4; d++) {
                            int nx = cur.x + dx[d];
                            int ny = cur.y + dy[d];
                            if (nx < 0 || nx >= 5 || ny < 0 || ny >= 5 || visited[nx][ny]) {
                                continue;
                            }
                            if (map[nx][ny] == map[i][j]) {
                                q.add(new Pos(nx, ny));
                                visited[nx][ny] = true;
                                same.add(new Pos(nx, ny));
                            }
                        }
                    }

                    if (same.size() >= 3) {
                        remove.addAll(same);
                        // System.out.println(remove);
                    }
                }
            }
        }
        return remove.size();
    }
    public static int[][] rotate(int sx, int sy, int r) {
        // System.out.println(sx + ", " + sy + ": "+ r);
        int[][] newMap  = new int[5][5];
        // 초기화
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                newMap[i][j] = map[i][j];
            }
        }
        if (r == 1) {
            // 90도 회전
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    newMap[sx+i][sy+j] = map[2+sx-j][sy+i];
                }
            }
        } else if (r == 2) {
            // 180도 회전
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    newMap[sx+i][sy+j] = map[2+sx-i][2+sy-j];
                }
            }
        } else {
            // 270도 회전
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    newMap[sx+i][sy+j] = map[sx+j][2+sy-i];
                }
            }
        }
        return newMap;
    }
    public static void print(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(Arrays.toString(arr[i]));
        }
    }
}