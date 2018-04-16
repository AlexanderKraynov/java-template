package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort (int array[]) {
    int size = array.length;
    if (size < 2)
      return;
    int mid = size / 2;
    int leftSize = mid;
    int rightSize = size - mid;
    int[] left = new int[leftSize];
    int[] right = new int[rightSize];
    for (int i = 0; i < mid; i++) {
      left[i] = array[i];

    }
    for (int i = mid; i < size; i++) {
      right[i - mid] = array[i];
    }
    sort(left);
    sort(right);
    merge(left, right, array);

  }
  public static void merge(int[] left, int[] right, int[] arr) {
    int leftSize = left.length;
    int rightSize = right.length;
    int i = 0, j = 0, k = 0;
    while (i < leftSize && j < rightSize) {
      if (left[i] <= right[j]) {
        arr[k] = left[i];
        i++;
        k++;
      } else {
        arr[k] = right[j];
        k++;
        j++;
      }
    }
    while (i < leftSize) {
      arr[k] = left[i];
      k++;
      i++;
    }
    while (j < rightSize) {
      arr[k] = right[j];
      k++;
      j++;
    }
  }


  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
