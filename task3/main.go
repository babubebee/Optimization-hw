package main

import (
	"errors"
	"fmt"
	"math"
	"strings"
)

func isBalanced(supply, demand []int) bool {
	sumSupply, sumDemand := sum(supply), sum(demand)
	return sumSupply == sumDemand
}

func northwestCorner(supply, demand []int, cost [][]int) ([][]int, int, error) {
	if sum(supply) == 0 || sum(demand) == 0 {
		return nil, 0, errors.New("The method is not applicable!")
	}

	n, m := len(supply), len(demand)
	solution := make([][]int, n)
	for i := range solution {
		solution[i] = make([]int, m)
	}

	i, j, totalCost := 0, 0, 0
	for i < n && j < m {
		minValue := min(supply[i], demand[j])
		solution[i][j] = minValue
		totalCost += minValue * cost[i][j]
		supply[i] -= minValue
		demand[j] -= minValue

		if supply[i] == 0 {
			i++
		}
		if demand[j] == 0 {
			j++
		}
	}
	return solution, totalCost, nil
}

func vogelsApproximation(supply, demand []int, cost [][]int) ([][]int, int, error) {
	n, m := len(supply), len(demand)
	solution := make([][]int, n)
	for i := range solution {
		solution[i] = make([]int, m)
	}

	remainingSupply := append([]int(nil), supply...)
	remainingDemand := append([]int(nil), demand...)
	totalCost := 0

	for sum(remainingSupply) > 0 && sum(remainingDemand) > 0 {
		rowPenalties, colPenalties := calculatePenalties(remainingSupply, remainingDemand, cost)
		row, col := -1, -1
		if maxPenalty(rowPenalties) >= maxPenalty(colPenalties) {
			row = maxPenaltyIndex(rowPenalties)
			col = minCostIndex(remainingDemand, cost[row])
		} else {
			col = maxPenaltyIndex(colPenalties)
			row = minCostIndex(remainingSupply, getColumn(cost, col))
		}
		minValue := min(remainingSupply[row], remainingDemand[col])
		solution[row][col] = minValue
		totalCost += minValue * cost[row][col]
		remainingSupply[row] -= minValue
		remainingDemand[col] -= minValue
	}
	return solution, totalCost, nil
}

func russellsApproximation(supply, demand []int, cost [][]int) ([][]int, int, error) {
	n, m := len(supply), len(demand)
	solution := make([][]int, n)
	for i := range solution {
		solution[i] = make([]int, m)
	}

	remainingSupply := append([]int(nil), supply...)
	remainingDemand := append([]int(nil), demand...)
	totalCost := 0

	for sum(remainingSupply) > 0 && sum(remainingDemand) > 0 {
		rowMax := rowMaxCosts(cost, remainingSupply)
		colMax := colMaxCosts(cost, remainingDemand)
		row, col := -1, -1
		minDelta := math.MaxInt32

		for i := 0; i < n; i++ {
			if remainingSupply[i] > 0 {
				for j := 0; j < m; j++ {
					if remainingDemand[j] > 0 {
						delta := cost[i][j] - rowMax[i] - colMax[j]
						if delta < minDelta {
							minDelta = delta
							row, col = i, j
						}
					}
				}
			}
		}

		minValue := min(remainingSupply[row], remainingDemand[col])
		solution[row][col] = minValue
		totalCost += minValue * cost[row][col]
		remainingSupply[row] -= minValue
		remainingDemand[col] -= minValue
	}
	return solution, totalCost, nil
}

func sum(arr []int) int {
	total := 0
	for _, val := range arr {
		total += val
	}
	return total
}

func calculatePenalties(supply, demand []int, cost [][]int) ([]int, []int) {
	rowPenalties := make([]int, len(supply))
	colPenalties := make([]int, len(demand))

	for i, row := range cost {
		if supply[i] > 0 {
			rowPenalties[i] = secondSmallest(row)
		}
	}

	for j := 0; j < len(demand); j++ {
		if demand[j] > 0 {
			column := getColumn(cost, j)
			colPenalties[j] = secondSmallest(column)
		}
	}

	return rowPenalties, colPenalties
}

func secondSmallest(arr []int) int {
	first, second := math.MaxInt32, math.MaxInt32
	for _, v := range arr {
		if v < first {
			second = first
			first = v
		} else if v < second && v != first {
			second = v
		}
	}
	return second
}

func maxPenalty(arr []int) int {
	maxVal := -1
	for _, val := range arr {
		if val > maxVal {
			maxVal = val
		}
	}
	return maxVal
}

func maxPenaltyIndex(arr []int) int {
	maxVal, index := -1, -1
	for i, val := range arr {
		if val > maxVal {
			maxVal = val
			index = i
		}
	}
	return index
}

func minCostIndex(demand []int, costs []int) int {
	minCost := math.MaxInt32
	index := -1
	for j, d := range demand {
		if d > 0 && costs[j] < minCost {
			minCost = costs[j]
			index = j
		}
	}
	return index
}

func getColumn(matrix [][]int, col int) []int {
	column := make([]int, len(matrix))
	for i, row := range matrix {
		column[i] = row[col]
	}
	return column
}

func rowMaxCosts(cost [][]int, remainingSupply []int) []int {
	rowMax := make([]int, len(remainingSupply))
	for i, row := range cost {
		if remainingSupply[i] > 0 {
			maxCost := -1
			for _, c := range row {
				if c > maxCost {
					maxCost = c
				}
			}
			rowMax[i] = maxCost
		}
	}
	return rowMax
}

func colMaxCosts(cost [][]int, remainingDemand []int) []int {
	colMax := make([]int, len(remainingDemand))
	for j := 0; j < len(remainingDemand); j++ {
		if remainingDemand[j] > 0 {
			column := getColumn(cost, j)
			maxCost := -1
			for _, c := range column {
				if c > maxCost {
					maxCost = c
				}
			}
			colMax[j] = maxCost
		}
	}
	return colMax
}

func callMethods(supply, demand []int, cost [][]int) {
	fmt.Println("Input Parameter Table:")

	header := []string{" ", "D1", "D2", "D3", "D4", "Supply"}

	fmt.Println("+----------+----------+----------+----------+----------+----------+")
	fmt.Printf("| %-8s | %-8s | %-8s | %-8s | %-8s | %-8s |\n", header[0], header[1], header[2], header[3], header[4], header[5])
	fmt.Println("|----------|----------|----------|----------|----------|----------|")

	for i, row := range cost {
		fmt.Printf("| S%-7d |", i+1)
		for _, val := range row {
			fmt.Printf(" %-8d |", val)
		}
		fmt.Printf(" %-8d |\n", supply[i])
		fmt.Println("|----------|----------|----------|----------|----------|----------|")
	}

	fmt.Printf("| Demand   |")
	for _, val := range demand {
		fmt.Printf(" %-8d |", val)
	}
	fmt.Println("		  |")
	fmt.Println("+----------+----------+----------+----------+----------+----------+")

	if !isBalanced(supply, demand) {
		fmt.Println("The problem is not balanced!")
		return
	}

	nwSolution, nwCost, nwErr := northwestCorner(append([]int(nil), supply...), append([]int(nil), demand...), cost)
	if nwErr != nil {
		fmt.Println(nwErr)
	} else {
		fmt.Println("Northwest Corner Solution:")
		for _, row := range nwSolution {
			fmt.Println(strings.Trim(fmt.Sprint(row), "[]"))
		}
		fmt.Printf("Total Cost: %d\n", nwCost)
	}

	vogelSolution, vogelCost, vogelErr := vogelsApproximation(append([]int(nil), supply...), append([]int(nil), demand...), cost)
	if vogelErr != nil {
		fmt.Println(vogelErr)
	} else {
		fmt.Println("Vogel's Approximation Solution:")
		for _, row := range vogelSolution {
			fmt.Println(strings.Trim(fmt.Sprint(row), "[]"))
		}
		fmt.Printf("Total Cost: %d\n", vogelCost)
	}

	russellSolution, russellCost, russellErr := russellsApproximation(append([]int(nil), supply...), append([]int(nil), demand...), cost)
	if russellErr != nil {
		fmt.Println(russellErr)
	} else {
		fmt.Println("Russell's Approximation Solution:")
		for _, row := range russellSolution {
			fmt.Println(strings.Trim(fmt.Sprint(row), "[]"))
		}
		fmt.Printf("Total Cost: %d\n", russellCost)
	}
}

// testBalancedProblem
func test1() {
	supply := []int{20, 30, 25}
	demand := []int{10, 25, 15, 25}
	cost := [][]int{
		{8, 6, 10, 9},
		{9, 12, 13, 7},
		{14, 9, 16, 5},
	}
	fmt.Println("Test 1")
	callMethods(supply, demand, cost)
	fmt.Println()
}

// testUnbalancedProblem
func test2() {
	supply := []int{20, 30, 30}
	demand := []int{10, 25, 15, 25}
	cost := [][]int{
		{8, 6, 10, 9},
		{9, 12, 13, 7},
		{14, 9, 16, 5},
	}
	fmt.Println("Test 2")
	callMethods(supply, demand, cost)
	fmt.Println()
}

func test3() {
	supply := []int{20, 30, 25}
	demand := []int{10, 25, 15, 25}
	cost := [][]int{
		{8, 6, 10, 9},
		{9, 12, 13, 7},
		{14, 9, 16, 5},
	}
	fmt.Println("Test 3")
	callMethods(supply, demand, cost)
	fmt.Println()
}

func main() {
	test1()
	test2()
	test3()
}
