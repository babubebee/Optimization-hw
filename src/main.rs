mod simplex_method_impl {
    use std::{collections::HashMap, f64::MAX};
    use itertools::Itertools;
    const EPS_DEFAULT: f64 = 1.0; // Need to define later

    fn print_init(c: &Vec<f64>, a: &Vec<Vec<f64>>, b: &Vec<f64>, e: f64) {
        print!("max (or min) z = ");
        print!("({}) * x1", c[0]);
        for i in 1..c.len() {
            print!(" + ({}) * x{}", c[i], i + 1);
        }
        println!();
        println!("subject to the constraints:");
        for i in 0..a.len() {
            print!("{}) ", i + 1);
            let mut f = false;
            for j in 0..a[i].len() {
                if a[i][j] != 0.0 {
                    if !f { f = true; }
                    else  { print!(" + "); }
                    print!("({}) * x{}", a[i][j], j + 1);
                }
            }
            println!(" <= {}", b[i]);
        }
    }
    #[derive(Debug)]
    struct Table {
        data: Vec<Vec<f64>>,
        rows: HashMap<String, usize>,
        columns: HashMap<String, usize>,
        x: usize,
        s: usize
    }
    pub enum Ret_State {
        solved,
        unbounded
    }
    impl Ret_State {
        pub fn to_string(&self) -> &str {
            match self {
                Ret_State::solved => { "Solved "}
                Ret_State::unbounded => { "Unbounded" }
            }
        }
    }
    enum State {
        unbound,
        finished,
        in_progress
    }
    impl Table {
        fn print(&self) {
            for i in 1..self.x + 1 {
                print!("\t x{i}");
            }
            for i in 1..self.s + 1 {
                print!("\t s{i}");
            }
            println!("\t value");
         
            for j in self.rows.keys().sorted().rev() {
                print!("{j}");
                for i in 1..self.x + 1 {
                    print!("\t {:.2}", self.get(j, format!("x{i}").as_str()));
                }
                for i in 1..self.s + 1 {
                    print!("\t {:.2}", self.get(j, format!("s{i}").as_str()));
                }
                println!("\t {:.2}", self.get(j, "v"));
            }
        }
        fn get(&self, a : &str, b : &str) -> f64 {
            self.data[*self.rows.get(a).expect(format!("Failed to get {}", a).as_str())][*self.columns.get(b).expect("Failed")]
        }
        fn change_row(&mut self, go_in: &str, go_out: &str) {
            if self.get(go_out, go_in) == 0.0 {
                panic!("Cannot change row, 0 found");
            }
            if let Some(v) = self.rows.remove(go_out) {
                self.rows.insert(String::from(go_in), v);
            }
            let coef = self.get(go_in, go_in);
            for i in self.columns.keys() {
                let v = self.get(go_in, i) / coef;
                self.data[*self.rows.get(go_in).expect("Failed")][*self.columns.get(i).expect("Failed")] = v;
            }
            for i in self.rows.keys() {
                if i != go_in {
                    let coef = self.get(i, go_in);
                    for j in self.columns.keys() {
                        let v = self.get(i, j) - coef * self.get(go_in, j);
                        self.data[*self.rows.get(i).expect("Failed")][*self.columns.get(j).expect("Failed")] = v;
                    }
                }
            }
        }
        fn var_change(&self) -> (String, String, State) {
            let mut m = 0.0;
            let mut s = String::new();
            for i in 1..self.x + 1 {
                if self.get("z", format!("x{i}").as_str()) < m {
                    m = self.get("z", format!("x{i}").as_str());
                    s = format!("x{i}");
                }                
            }
            if (s.is_empty()) { return (String::new(), String::new(), State::finished) }
            let mut n: f64 = MAX;
            let mut out = String::new();
            for i in self.rows.keys() {
                if i == "z" { continue; }
                if self.get(i.as_str(), "v") / self.get(i.as_str(), s.as_str()) <= n && self.get(i.as_str(), s.as_str()) >= 0.0 {
                    n = self.get(i.as_str(), "v") / self.get(i.as_str(), s.as_str());
                    out = i.clone();
                }
            }
            if (out.is_empty()) { return (String::new(), String::new(), State::unbound) }
            let go_in = s;
            let go_out = out;
            (go_in, go_out, State::in_progress)
        }
        fn init(c: &Vec<f64>, a: &Vec<Vec<f64>>, b: &Vec<f64>) -> Self {
            let mut table = Self {
                data: Vec::new(),
                rows: HashMap::new(),
                columns: HashMap::new(),
                x: c.len(),
                s: b.len()
            };
            for i in 1..c.len() + 1 {
                table.columns.insert(format!("x{}", i), i - 1);
            }
            for i in 1..a.len() + 1 {
                table.columns.insert(format!("s{}", i),table.columns.len());
            }
            table.columns.insert(String::from("v"), table.columns.len());
            table.rows.insert(String::from("z"), 0);
            for i in 1..a.len() + 1 {
                table.rows.insert(format!("s{}", i), i);
            }
            // Now fill data
            table.data.resize(table.rows.len(), vec![0.0;table.columns.len()]);
            for i in 0..table.x {
                table.data[0][i] = -c[i];
            }
            for i in 1..table.s + 1 {
                for j in 0..table.x {
                    table.data[i][j] = a[i - 1][j];
                }
            }
            for i in 1..table.s + 1 {
                table.data[i][table.x + i - 1] = 1.0;
            }
            for i in 1..table.s + 1 {
                table.data[i][table.columns.len() - 1] = b[i - 1];
            }
            table
        }
        fn result(&self) -> f64 {
            self.data[0][self.data[0].len() - 1]
        }
        fn result_vec(&self) -> HashMap<String, f64> {
            let mut ans  = HashMap::new();
            for i in self.rows.keys() {
                if i.starts_with("x") {
                    ans.insert(i.clone(),self.get(i, "v"));
                }
            }
            ans
        }
    }

    pub fn simplex_method(c: Vec<f64>, a: Vec<Vec<f64>>, b: Vec<f64>, e: Option<f64>) -> (Ret_State, Option<HashMap<String, f64>>, Option<f64>) {
        let e = e.unwrap_or(EPS_DEFAULT);
        print_init(&c, &a, &b, e);
        let mut table = Table::init(&c, &a, &b);
        let mut it = 0;
        loop {
            println!("-----------");
            let (a, b, c) = table.var_change();
            match c {
                State::in_progress => {},
                State::unbound => {
                    // println!("The problem is unbounded!");
                    return (Ret_State::unbounded, None, None);
                },
                State::finished => { 
                    // println!("Finished!");
                    break
                }
            }
            println!("Iteration #{}:", it);
            println!("{} enters {}!", a, b);
            table.print();
            table.change_row(a.as_str(), b.as_str());
            it += 1;
        }
        (Ret_State::solved, Some(table.result_vec()), Some(table.result()))
    }
}

use simplex_method_impl::{simplex_method, Ret_State};

fn main() {
    let c = vec![9.0, 10.0, 16.0];
    let a = vec![
        vec![-18.0, -15.0, -12.0],
        vec![-6.0, 0.0, 0.0],
        vec![-5.0, -3.0, 3.0]
    ];
    let b = vec![360.0, 192.0, 180.0];
    let (a, b, c) = simplex_method(c, a, b, None);
    match a {
        Ret_State::solved => {
            println!("Solved!");
            let b = b.unwrap();
            let c = c.unwrap();
            println!("The maximum is {c} and the solution is {:?}", b);
        },
        Ret_State::unbounded => {
            println!("Could not solve the problem since it is unbounded!");
        }
    }
}